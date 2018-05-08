package com.bclould.tocotalk.crypto.otr;

import android.util.Log;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrKeyManagerDefaultImpl;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.SessionID;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Hashtable;

/**
 * Created by GIjia on 2018/5/3.
 */

public class OtrEngineHostImpl implements OtrEngineHost {

    private OtrPolicy mPolicy;
    private Hashtable<SessionID, String> mSessionResources;
    private OtrKeyManagerDefaultImpl mOtrKeyManager;
    public String lastInjectedMessage="";

    public OtrEngineHostImpl(OtrPolicy policy, OtrKeyManagerDefaultImpl otrKeyManagerDefault) throws IOException {
        mPolicy = policy;
        mSessionResources = new Hashtable<SessionID, String>();
        mOtrKeyManager=otrKeyManagerDefault;
    }

    public void putSessionResource(SessionID session, String resource) {
        mSessionResources.put(session, resource);
    }

    public void removeSessionResource(SessionID session) {
        mSessionResources.remove(session);
    }

    public void setSessionPolicy(OtrPolicy policy) {
        mPolicy = policy;
    }

    @Override
    public void injectMessage(SessionID sessionID, String msg) {
        lastInjectedMessage=msg;

    }

    @Override
    public void showWarning(SessionID sessionID, String warning) {
        Log.i("fengjian",sessionID.toString() + ": WARNING=" + warning);
    }

    @Override
    public void showError(SessionID sessionID, String error) {
        Log.i("fengjian",sessionID.toString() + ": ERROR=" + error);
    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID sessionID) {
        return mPolicy;
    }

    @Override
    public KeyPair getKeyPair(SessionID sessionID) {
        KeyPair kp = null;
        kp = mOtrKeyManager.loadLocalKeyPair(sessionID);

        if (kp == null) {
            mOtrKeyManager.generateLocalKeyPair(sessionID);
            kp = mOtrKeyManager.loadLocalKeyPair(sessionID);
        }
        return kp;
    }
}
