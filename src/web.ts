import { WebPlugin } from '@capacitor/core';

import type {
  CancelStatus,
  FileDownloadPlugin,
  FileDownloadOptions,
  FileDownloadResponse,
  PermissionStatus,
} from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
  openSetting(): Promise<void> {
    throw new Error('Method not implemented onWeb.');
  }
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  async download(_options?: FileDownloadOptions): Promise<FileDownloadResponse> {
    return { path: '' };
  }
  async cancel(): Promise<void> {
    return;
  }
  async isCanceled(): Promise<CancelStatus> {
    return {
      isCanceled: false
    };
  }
  async checkPermissions(): Promise<PermissionStatus> {
    return {
      storage: "prompt"
    }
  }
  async requestPermissions(): Promise<PermissionStatus> {
    return {
      storage: "prompt"
    }
  }
}
