# capacitor-plugin-filedownload

a file download plugin for Capacitor3

## Install

```bash
npm install capacitor-plugin-filedownload
npx cap sync
```
eg:
```typescript
import { FileDownload } from "capacitor-plugin-filedownload";

FileDownload.download({
    uri: "http://www.xxxxx.com/file/rvh.apk",
    fileName: "release.apk"
}).then((res) => {
    console.log(res.path);
}).catch(err => {
    console.log(err);
})

···
```
## API

<docgen-index>

* [`download(...)`](#download)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### download(...)

```typescript
download(options: FileDownloadOptions) => any
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#filedownloadoptions">FileDownloadOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### FileDownloadOptions

| Prop           | Type                |
| -------------- | ------------------- |
| **`uri`**      | <code>string</code> |
| **`fileName`** | <code>string</code> |

</docgen-api>

感谢 [https://github.com/veluxa/capacitor-plugin-file-downloader](https://github.com/veluxa/capacitor-plugin-file-downloader)