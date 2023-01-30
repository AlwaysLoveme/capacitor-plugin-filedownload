export interface FileDownloadPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
