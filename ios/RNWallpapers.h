
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNRNWallpapersSpec.h"

@interface RNWallpapers : NSObject <NativeRNWallpapersSpec>
#else
#import <React/RCTBridgeModule.h>

@interface RNWallpapers : NSObject <RCTBridgeModule>
#endif

@end
