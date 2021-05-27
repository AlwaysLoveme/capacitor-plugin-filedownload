import { WebPlugin } from '@capacitor/core';

import type { FileDownloadPlugin, FileDownloadOptions, FileDownloadResponse } from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
  async download(options: FileDownloadOptions): Promise<FileDownloadResponse> {
    return options as unknown as FileDownloadResponse;
  }
}
