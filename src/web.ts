import { WebPlugin } from '@capacitor/core';

import type { FileDownloadPlugin } from './definitions';

export class FileDownloadWeb extends WebPlugin implements FileDownloadPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
