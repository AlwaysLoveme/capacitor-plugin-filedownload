import { WebPlugin } from '@capacitor/core';

import type { FileDownloadPlugin, FileDownloadOptions } from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
  async download(options: FileDownloadOptions): Promise<any> {
    console.log('ECHO', options);
    return options;
  }
}
