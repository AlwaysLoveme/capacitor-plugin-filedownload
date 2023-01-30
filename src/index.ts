import { registerPlugin } from '@capacitor/core';

import type { FileDownloadPlugin } from './definitions';

const FileDownload = registerPlugin<FileDownloadPlugin>('FileDownload', {
  web: () => import('./web').then(m => new m.FileDownloadWeb()),
});

export * from './definitions';
export { FileDownload };
