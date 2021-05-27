import Foundation
import Alamofire
import Capacitor
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
typealias JSObject = [String:Any]
@objc(FileDownloadPlugin)
public class FileDownloadPlugin: CAPPlugin {
    private let implementation = FileDownload()
    
    var downloadRequest:DownloadRequest!//下载请求对象
    var _call:CAPPluginCall!
    var fileUrl:URL!
    
    @objc func download(_ call:CAPPluginCall) {
        self._call = call;
        let url = call.getString("uri") ?? ""
        let filename = call.getString("fileName") ?? ""
        
        let destination: DownloadRequest.Destination = { _, response in
            let documentsUrl = FileManager.default.urls(for: .documentDirectory, in: FileManager.SearchPathDomainMask.userDomainMask).first
            self.fileUrl = documentsUrl?.appendingPathComponent(filename)
            
            return (self.fileUrl, [.removePreviousFile, .createIntermediateDirectories])
        }
        
        self.downloadRequest =  AF.download(url, to: destination)
        self.downloadRequest.responseData(completionHandler: downloadResponse)
    }
    
    //根据下载状态处理
    func downloadResponse(response: AFDownloadResponse<Data>){
        switch response.result {
        case .success:
            var data = JSObject()
            data["path"] = self.fileUrl.absoluteString;
            self._call.resolve(data)
            break;
        case .failure:
            self._call.reject("下载失败！请检查URL是否正确")
        default:
            break
        }
    }
}
