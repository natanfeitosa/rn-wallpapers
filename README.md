# rn-wallpapers

A lightweight library for setting wallpapers

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

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
