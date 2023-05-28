import Foundation
import Capacitor
import Alamofire

@objc(FileDownloadPlugin)
public class FileDownloadPlugin: CAPPlugin {
    var downloadRequest: DownloadRequest?
    var _call: CAPPluginCall?
    var fileUrl: URL!
    
    var isCancelled: Bool! = false;
    
    let maxRetryCount = 3
    var currentRetryCount = 0
    
    @objc func download(_ call: CAPPluginCall) {
        self._call = call
        guard let url = call.getString("url") else {
            call.reject("Invalid URL")
            return
        }
        let fileName = call.getString("fileName") ?? ""
        let destination = call.getString("destination") ?? "DOCUMENT"
        let headers = call.getObject("headers") as? [String: String] ?? [:]
        let body = call.getObject("body")
        let method = !headers.isEmpty || body != nil ? HTTPMethod.post : HTTPMethod.get
        
        let fileDirectory = getFileDirectory(destination: destination)
        let newDestination = createDestination(directory: fileDirectory, fileName: fileName)
        
        self.downloadRequest = AF.download(url, method: method, parameters: body, headers: HTTPHeaders(headers), to: newDestination);
        self.downloadRequest?.downloadProgress(closure: self.uploadProgress)
        self.downloadRequest?.responseData(completionHandler: self.downloadResponse)
        
    }
    
    @objc func cancel(_ call: CAPPluginCall) {
        self.downloadRequest?.cancel()
        self.downloadRequest = nil
        self._call?.resolve()
        self._call = nil
    }
    
    @objc func isCanceled(_ call: CAPPluginCall) {
        let isCancelled = downloadRequest?.isCancelled ?? false;
        self.isCancelled = isCancelled;
        call.resolve(["isCanceled": isCancelled])
    }
    
    func getFileDirectory(destination: String) -> FileManager.SearchPathDirectory {
        switch destination {
        case "DOCUMENT", "DATA", "EXTERNAL", "EXTERNAL_STORAGE":
            return .documentDirectory
        case "LIBRARY":
            return .libraryDirectory
        case "CACHE":
            return .cachesDirectory
        default:
            return .documentDirectory
        }
    }
    
    func createDestination(directory: FileManager.SearchPathDirectory, fileName: String) -> DownloadRequest.Destination {
        let destination: DownloadRequest.Destination = { _, response in
            let fileManager = FileManager.default
            let directoryURL = fileManager.urls(for: directory, in: .userDomainMask)[0]
            let fileURL = directoryURL.appendingPathComponent(fileName)
            
            
            
            let correctedPath = self.correctFilePath(fileURL.path)
            self.fileUrl = URL(fileURLWithPath: correctedPath)
            
            // Create directory if it doesn't exist
            let directoryPath = fileURL.deletingLastPathComponent().path
            if !fileManager.fileExists(atPath: directoryPath) {
                try? fileManager.createDirectory(atPath: directoryPath, withIntermediateDirectories: true, attributes: nil)
            }
            
            return (self.fileUrl, [.createIntermediateDirectories, .removePreviousFile])
        }
        return destination
    }
    
    func correctFilePath(_ path: String) -> String {
        var correctedPath = path
        let isFileUrl = path.hasPrefix("file:///")
        let hasExtraSlash = path.contains("//")
        
        if isFileUrl {
            correctedPath = correctedPath.replacingOccurrences(of: "file:///", with: "file://")
        }
        
        if hasExtraSlash {
            correctedPath = correctedPath.replacingOccurrences(of: "//", with: "/")
        }
        
        return correctedPath
    }
    
    func uploadProgress(progress: Progress) {
        if(self.isCancelled) {
            return;
        }
        let data: [String: Any] = ["progress": progress.fractionCompleted * 100]
        self.notifyListeners("downloadProgress", data: data)
    }
    
    func downloadResponse(response: AFDownloadResponse<Data>) {
        switch response.result {
        case .success:
            var data = JSObject()
            if let fileUrl = response.fileURL {
                data["path"] = fileUrl.absoluteString
            }
            self._call?.resolve(data)
            self.currentRetryCount = 0
        case .failure(let error):
            if(self.isCancelled) {
                return;
            }
            if self.currentRetryCount < self.maxRetryCount {
                self.currentRetryCount += 1
                self.retryDownload()
            } else {
                self._call?.reject("下载失败！", error.localizedDescription, error)
                self.currentRetryCount = 0
            }
        }
        
        self.downloadRequest = nil
        self._call = nil
    }
    
    func retryDownload() {
        self.downloadRequest?.resume()
    }
    
    
}
