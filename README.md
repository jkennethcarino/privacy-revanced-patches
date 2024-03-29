# Privacy ReVanced Patches

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/jkennethcarino/privacy-revanced-patches/release.yml)
![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)

This repository contains my personal collection of ReVanced Patches.

## ❓ About

Patches are small modifications to Android apps that allow you to change the behavior of or add new features,
block analytics and trackers, and much more.

## 🧩 Features

Some of the features the patches provide are:

* **Remove internet permission**: Remove unnecessary internet permission from apps that can function without internet access.
* **Disable Firebase Analytics**: Permanently disable the collection of Analytics data, all associated
    broadcast receivers and services will also be removed.
* **Disable WebView metrics collection**: Disable the collection of diagnostic data or usage statistics
    that are uploaded to Google.
* **Bypass Gboard signature verification**: Bypass the signature verification when the app starts up,
    allowing you to easily remove internet access or change its package name.
* **And much more!**

## 🚀 Getting started

You can use [ReVanced CLI](https://github.com/ReVanced/revanced-cli) or [ReVanced Manager](https://github.com/ReVanced/revanced-manager) to use Privacy ReVanced Patches.

To use these patches with the ReVanced Manager, follow the steps below:
1. Open the **ReVanced Manager** app.
2. Select the **Settings** tab.
3. In the **Data sources** section, enable the **Use alternative sources** option and click on the **Alternative sources**.
4. Set the following alternative sources:
    - Patches organization: `jkennethcarino`
    - Patches source: `privacy-revanced-patches`
    - Integration organization: `jkennethcarino`
    - Integration source: `privacy-revanced-integrations`

<img src="/assets/rvm-alternative_sources.png" width="300px" />

## 📚 Everything else

### 🛠️ Building

To build Privacy ReVanced Patches, you can follow the [ReVanced documentation](https://github.com/ReVanced/revanced-documentation).

## 📜 License

Privacy ReVanced Patches is licensed under the GPLv3 license. Please see the [license file](LICENSE) for more information.
[tl;dr](https://www.tldrlegal.com/license/gnu-general-public-license-v3-gpl-3) you may copy, distribute and modify Privacy ReVanced Patches as long as you track changes/dates in source files.
Any modifications to Privacy ReVanced Patches must also be made available under the GPL,
along with build & install instructions.
