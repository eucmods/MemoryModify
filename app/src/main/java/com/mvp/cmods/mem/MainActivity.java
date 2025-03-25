/**
 * @Author CMODs
 * @AIDE AIDE+
*/
package com.mvp.cmods.mem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import com.mvp.cmods.mem.mvp.*;

public class MainActivity extends Activity { 

EditText pkg_gameh,lib_name,offset_name,hex_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		pkg_gameh = findViewById(R.id.pkg);
		lib_name = findViewById(R.id.libname);
		offset_name = findViewById(R.id.offsetname);
		hex_r = findViewById(R.id.hexr);
    }
	
	public void CM_ODs(View h){
		HackUtils.modifyProcessMemory(MainActivity.this
			, pkg_gameh.getText().toString()
			 , lib_name.getText().toString()
			 , Long.parseLong(offset_name.getText().toString(), 16)
			 , hex_r.getText().toString()
		 );
		 Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_SHORT).show();
	}
}
