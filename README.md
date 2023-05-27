# capacitor-plugin-filedownload

<p align="left">
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-filedownload">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-filedownload/latest.svg">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-filedownload">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-filedownload.svg"/>
  </a>
</p>

<p>
a file download plugin for capacitor3.0+
</p>

> not support Capacitor5

> since version `1.0.6` , the `uri` option change to `url`

## Install

```bash
npm install capacitor-plugin-filedownload
npx cap sync
```

eg:

```ts
import { FileDownload } from "capacitor-plugin-filedownload";

const download = async () => {
  FileDownload.download({
    url: "http://www.xxxxx.com/file/rvh.apk",
    fileName: "release.apk",
    // headers for http request with POST method
    headers: {},
    // parameter for http request with POST method
    body: {},
    // only works on Android, deprecated since 1.0.6
    downloadTitle: 'downloading',
    // only works on Android, deprecated since 1.0.6
    downloadDescription: 'file is downloading',
  }).then((res) => {
    console.log(res.path);
  }).catch(err => {
    console.log(err);
  })
}


// cancel download
const cancelDownload = async () => {
  await FileDownload.cancel();
}


// get download status
const getDownloadStatus = () => {
  const {isCanceled} = await FileDownload.isCanceled();
  console.log(isCanceled);
}


// event listener for downloadProgress
const onDownloadProgress = async () => {
  const eventListener = await FileDownload.addListener('downloadProgress', data =>{
    console.log(data.progress);
  })

  // remove eventListener
  eventListener.remove();
}

...
```

if you wish to open the file, you can install this plugin:
https://github.com/capacitor-community/file-opener

## API

<docgen-index>

* [`download(...)`](#download)
* [`cancel()`](#cancel)
* [`isCanceled()`](#iscanceled)
* [`addListener('downloadProgress', ...)`](#addlistenerdownloadprogress)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### download(...)

```typescript
download(options: FileDownloadOptions) => Promise<FileDownloadResponse>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#filedownloadoptions">FileDownloadOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#filedownloadresponse">FileDownloadResponse</a>&gt;</code>

--------------------


### cancel()

```typescript
cancel() => Promise<void>
```

cancel download

--------------------


### isCanceled()

```typescript
isCanceled() => Promise<CancelStatus>
```

get status of download

**Returns:** <code>Promise&lt;<a href="#cancelstatus">CancelStatus</a>&gt;</code>

--------------------


### addListener('downloadProgress', ...)

```typescript
addListener(eventName: 'downloadProgress', listenerFunc: (progress: FileDownloadProgress) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                         |
| ------------------ | -------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'downloadProgress'</code>                                                              |
| **`listenerFunc`** | <code>(progress: <a href="#filedownloadprogress">FileDownloadProgress</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### Interfaces


#### FileDownloadResponse

| Prop       | Type                |
| ---------- | ------------------- |
| **`path`** | <code>string</code> |


#### FileDownloadOptions

| Prop                      | Type                                                             | Description                                                                          | Default                             |
| ------------------------- | ---------------------------------------------------------------- | ------------------------------------------------------------------------------------ | ----------------------------------- |
| **`url`**                 | <code>string</code>                                              |                                                                                      |                                     |
| **`fileName`**            | <code>string</code>                                              |                                                                                      |                                     |
| **`destination`**         | <code><a href="#destination">Destination</a></code>              | Download file destination                                                            | <code>ios default: Documents</code> |
| **`headers`**             | <code><a href="#record">Record</a>&lt;string, string&gt;</code>  | request headers, when headers has value, url must be a http request with POST method |                                     |
| **`body`**                | <code><a href="#record">Record</a>&lt;string, unknown&gt;</code> | request body, when body has value, url must be a http request width POST method      |                                     |
| **`downloadTitle`**       | <code>string</code>                                              | Downloader Title， Only Android                                                       |                                     |
| **`downloadDescription`** | <code>string</code>                                              | Downloader Description， Only Android                                                 |                                     |


#### CancelStatus

| Prop             | Type                 |
| ---------------- | -------------------- |
| **`isCanceled`** | <code>boolean</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### FileDownloadProgress

| Prop           | Type                |
| -------------- | ------------------- |
| **`progress`** | <code>number</code> |


### Type Aliases


#### Destination

download destination , on android default is "DOWNLOAD", on ios default is "DOCUMENT"

<code>"DOCUMENT" | "EXTERNAL" | "EXTERNAL_STORAGE" | "DATA" | "CACHE"</code>


#### Record

Construct a type with a set of properties K of type T

<code>{ [P in K]: T; }</code>

</docgen-api>
