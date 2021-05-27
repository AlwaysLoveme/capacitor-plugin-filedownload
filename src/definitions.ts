export interface FileDownloadPlugin {
  download(options: FileDownloadOptions): Promise<FileDownloadResponse>;
}
export interface FileDownloadOptions {
  uri: string;
  fileName: string;
}
export interface FileDownloadResponse {
  path: string;
}