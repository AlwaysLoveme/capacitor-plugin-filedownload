import type { PluginListenerHandle } from '@capacitor/core';

export interface FileDownloadPlugin {
  download(options: FileDownloadOptions): Promise<FileDownloadResponse>;
  /**
   * cancel download
   */
  cancel(): Promise<void>;
  /**
   * get status of download
   */
  isCanceled(): Promise<CancelStatus>;
  addListener(
    eventName: 'downloadProgress',
    listenerFunc: (progress: FileDownloadProgress) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export interface CancelStatus {
  isCanceled: boolean
}

/**
 * download destination , on android default is "DOWNLOAD", on ios default is "DOCUMENT"
 */
export type Destination = "DOCUMENT" | "EXTERNAL" | "EXTERNAL_STORAGE" | "DATA" | "CACHE";

export interface FileDownloadOptions {
  url: string;
  fileName: string;
  /**
   * Download file destination
   * @default ios default: Documents
   * @default android default: External Storage
   */
  destination?: Destination;
  /**
   * request headers, when headers has value, url must be a http request with POST method
   */
  headers?: Record<string, string>,
  /**
   * request body, when body has value, url must be a http request width POST method
   */
  body?: Record<string, unknown>,
  /**
   * Downloader Title， Only Android
   * @deprecated since 1.0.6
   */
  downloadTitle?: string;
  /**
   * Downloader Description， Only Android
   * @deprecated since 1.0.6
   */
  downloadDescription?: string;
}
export interface FileDownloadResponse {
  path: string;
}
export interface FileDownloadProgress {
  progress: number;
}
