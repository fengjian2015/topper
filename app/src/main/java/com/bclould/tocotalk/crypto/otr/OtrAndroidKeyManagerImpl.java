package com.bclould.tocotalk.crypto.otr;

import android.util.Base64;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerDefaultImpl;
import net.java.otr4j.OtrKeyManagerStore;
import net.java.otr4j.session.SessionID;

import java.util.Properties;

public class OtrAndroidKeyManagerImpl implements OtrKeyManagerStore {
    private final Properties properties = new Properties();

    public  OtrAndroidKeyManagerImpl(SessionID aliceSessionID) {
//        try {
//            OtrKeyManager keyManager = new OtrKeyManagerDefaultImpl("otr.properties");
//            keyManager.generateLocalKeyPair(aliceSessionID);
//            keyManager.verify(aliceSessionID);
//            assert (keyManager.isVerified(aliceSessionID));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public byte[] getPropertyBytes(String id) {
        try {
            String value = properties.getProperty(id);
            return Base64.decode(value, Base64.NO_WRAP);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean getPropertyBoolean(String id, boolean defaultValue) {
        try {
            return Boolean.valueOf(properties.get(id).toString());
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void setProperty(String id, byte[] value) {
        properties.setProperty(id, Base64.encodeToString(value, Base64.NO_WRAP));
    }

    @Override
    public void setProperty(String id, boolean value) {
        properties.setProperty(id, value+"");
    }

    @Override
    public void removeProperty(String id) {
        properties.remove(id);
    }
}
