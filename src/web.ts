import { WebPlugin } from '@capacitor/core';

import type {
  CancelStatus,
  FileDownloadPlugin,
  FileDownloadOptions,
  FileDownloadResponse,
} from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
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
}
