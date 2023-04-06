import type { PluginListenerHandle } from '@capacitor/core';

export interface FileDownloadPlugin {
  download(options: FileDownloadOptions): Promise<FileDownloadResponse>;
  addListener(eventName: 'downloadProgress', listenerFunc: (progress: FileDownloadProgress) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export interface FileDownloadOptions {
  uri: string;
  fileName: string;
  /**
   * Downloader Title， Only Android
   */
  downloadTitle?: string;
  /**
   * Downloader Description， Only Android
   */
  downloadDescription?: string;
}
export interface FileDownloadResponse {
  path: string;
}
export interface FileDownloadProgress {
  progress: number;
}