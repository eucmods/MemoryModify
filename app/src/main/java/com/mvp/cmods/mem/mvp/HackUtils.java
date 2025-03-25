/**
 * @Author CMODs
 * @AIDE AIDE+
*/
package com.mvp.cmods.mem.mvp;

import android.content.Context;
import android.content.Intent;

public class HackUtils {
    public static void modifyProcessMemory(Context context, String packageName, String libraryName, long offset, String hexValue) {
        Intent intent = new Intent(context, MemoryModifyService.class);
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.putExtra("LIBRARY_NAME", libraryName);
        intent.putExtra("OFFSET", offset);
        intent.putExtra("HEX_VALUE", hexValue);
        context.startService(intent);
    }
}

