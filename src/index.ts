import { NativeModules, Platform, PermissionsAndroid } from 'react-native';
import type { Permission } from 'react-native';

const LINKING_ERROR =
  `The package 'rn-wallpapers' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RNWallpapers = NativeModules.RNWallpapers
  ? NativeModules.RNWallpapers
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export enum TYPE_SCREEN {
  /**
   * Sets the wallpaper on Home Screen only
   */
  HOME = 1 << 0,
  /**
   * Sets the wallpaper on Lock Screen only
   */
  LOCK = 1 << 1,
  /**
   * Sets the wallpaper on both screen
   */
  BOTH = TYPE_SCREEN.HOME | TYPE_SCREEN.LOCK,
}

type setWallpaperReturn = Promise<Record<'status' | 'msg' | 'source', string>>;

/**
 * A helper to see if you can choose the type screen to define a wallpaper
 *
 * You can use together with `TYPE_SCREEN`
 *
 * @returns {boolean}
 */
export function canChooseTypeScreen(): boolean {
  if (Platform.OS !== 'android') return false;
  return Platform.Version >= 24;
}

/**
 * Check if the app has `SET_WALLPAPER` permission
 *
 * @returns {Promise<boolean>}
 */
export async function hasCorrectPermission(): Promise<boolean> {
  const permission = 'android.permission.SET_WALLPAPER' as Permission;
  return await PermissionsAndroid.check(permission);
}

export async function setWallpaper(
  params: { uri: string; headers?: Record<string, any> },
  type: TYPE_SCREEN = TYPE_SCREEN.HOME
): setWallpaperReturn {
  if (!(await hasCorrectPermission())) {
    throw new Error('SET_WALLPAPER permission needed');
  }

  if (typeof type !== 'number' || type < TYPE_SCREEN.HOME) {
    type = TYPE_SCREEN.HOME;
  }

  return RNWallpapers.setWallpaper(params, type);
}
