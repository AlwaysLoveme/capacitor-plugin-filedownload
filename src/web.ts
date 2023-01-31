import { WebPlugin } from '@capacitor/core';

import type {
  FileDownloadPlugin,
  FileDownloadOptions,
  FileDownloadResponse,
} from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
  async download(options?: FileDownloadOptions): Promise<FileDownloadResponse> {
    console.log('配置项', options);
    return { path: '' };
  }
}
