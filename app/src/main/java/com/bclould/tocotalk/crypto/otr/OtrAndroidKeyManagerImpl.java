package com.bclould.tocotalk.crypto.otr;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerDefaultImpl;
import net.java.otr4j.OtrKeyManagerStore;
import net.java.otr4j.session.SessionID;

import java.io.File;
import java.util.Properties;

public class OtrAndroidKeyManagerImpl implements OtrKeyManagerStore {
    private final Properties properties = new Properties();
    private File otrKeystoreAES;
    public OtrAndroidKeyManagerImpl(){

    }

//    public  OtrAndroidKeyManagerImpl(SessionID aliceSessionID) {
//        try {
//            otrKeystoreAES = new File(Environment.getExternalStorageDirectory(), aliceSessionID.toString().substring(0,2)+"otr.properties");
//            OtrKeyManager keyManager = new OtrKeyManagerDefaultImpl(otrKeystoreAES.getAbsolutePath());
//            keyManager.generateLocalKeyPair(aliceSessionID);
//            keyManager.verify(aliceSessionID);
//            assert (keyManager.isVerified(aliceSessionID));
//            return (OtrKeyManagerDefaultImpl) keyManager;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
    public OtrKeyManagerDefaultImpl OtrAndroidKeyManagerImpl(SessionID aliceSessionID, Context context) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/otr");
            if (!file.exists()) {
                file.mkdirs();
            }
            otrKeystoreAES = new File(context.getFilesDir().getAbsolutePath() + "/otr/"+aliceSessionID.toString()+".properties");
            OtrKeyManager keyManager = new OtrKeyManagerDefaultImpl(otrKeystoreAES.getAbsolutePath());
            if (keyManager.loadLocalKeyPair(aliceSessionID)==null) {
                keyManager.generateLocalKeyPair(aliceSessionID);
            }
            keyManager.verify(aliceSessionID);
            assert (keyManager.isVerified(aliceSessionID));
            return (OtrKeyManagerDefaultImpl) keyManager;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] getPropertyBytes(String id) {
        String value = properties.getProperty(id);
        return Base64.decode(value,Base64.NO_WRAP);
    }

    @Override
    public boolean getPropertyBoolean(String id, boolean defaultValue) {
        try {
            return Boolean.valueOf(properties.get(id).toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public void setProperty(String id, byte[] value) {
        properties.setProperty(id, Base64.encodeToString(value,Base64.NO_WRAP));
    }

    @Override
    public void setProperty(String id, boolean value) {
        properties.setProperty(id, "true");
    }

    @Override
    public void removeProperty(String id) {
        properties.remove(id);
    }
}
