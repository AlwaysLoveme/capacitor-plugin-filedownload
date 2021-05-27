export interface FileDownloadPlugin {
  download(options: FileDownloadOptions): Promise<{ path: string }>;
}
export interface FileDownloadOptions {
  uri: string;
  fileName: string;
}