# rn-wallpapers

<p align="center">A lightweight library for setting wallpapers with React Native</p>
<div align="center">
  <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/natanfeitosa/rn-wallpapers">
  <img alt="Version in NPM" src="https://img.shields.io/npm/v/rn-wallpapers">
  <img alt="License in GitHub" src="https://img.shields.io/github/license/natanfeitosa/rn-wallpapers">
  <img alt="Downloads in NPM" src="https://img.shields.io/npm/dm/rn-wallpapers">
</div>

## Installation

```sh
npm install rn-wallpapers

// or with yarn
yarn add rn-wallpapers
```

## Usage

```js
import { setWallpaper, TYPE_SCREEN } from 'rn-wallpapers';

// ...


await setWallpaper(
  {
    uri: 'https://i0.wp.com/techwek.com/wp-content/uploads/2021/01/wallpaper-gotas-de-chuva.jpg',
    headers: {
      // your custom headers ...
    }
  },
  TYPE_SCREEN.LOCK // Sets the wallpaper on Lock Screen only
);

await setWallpaper(
  {
    uri: 'https://i0.wp.com/techwek.com/wp-content/uploads/2021/01/wallpaper-gotas-de-chuva.jpg'
  },
  TYPE_SCREEN.HOME // (default) Sets the wallpaper on Home Screen only
);

await setWallpaper(
  {
    uri: 'https://i0.wp.com/techwek.com/wp-content/uploads/2021/01/wallpaper-gotas-de-chuva.jpg'
  },
  TYPE_SCREEN.BOTH // Sets the wallpaper on both screen
);
```

> If you are using `Expo Go`, you may need to add the `SET_WALLPAPER` permission to your [permissions array](https://docs.expo.dev/versions/latest/config/app/#permissions)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
