/*
 * Copyright (c) Libly - Terl Tech Ltd  • 04/08/2019, 22:41 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.hydride;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyExchangeKKTest extends BaseTest {

    private String context = "context1";
    private byte[] contextBytes = context.getBytes();

    private String message = "This is a message that will be encrypted.";
    private byte[] messageBytes = message.getBytes();

    @Test
    public void keyExchange() {
        // Generate server and client long-term keypairs
        Hydrogen.HydroKxKeyPair serverKeyPair = new Hydrogen.HydroKxKeyPair();
        Hydrogen.HydroKxKeyPair clientKeyPair = new Hydrogen.HydroKxKeyPair();
        hydrogen.hydro_kx_keygen(serverKeyPair);
        hydrogen.hydro_kx_keygen(clientKeyPair);

        // Client: Initiate a key exchange
        byte[] packet1 = new byte[Hydrogen.HYDRO_KX_KK_PACKET1BYTES];
        Hydrogen.HydroKxState stateClient = new Hydrogen.HydroKxState();
        hydrogen.hydro_kx_kk_1(stateClient, packet1, serverKeyPair.getPublicKey(), clientKeyPair);

        // Server: process the initial request from the client, and compute the session keys
        byte[] packet2 = new byte[Hydrogen.HYDRO_KX_KK_PACKET2BYTES];
        Hydrogen.HydroKxSessionKeyPair serverSession = new Hydrogen.HydroKxSessionKeyPair();
        hydrogen.hydro_kx_kk_2(serverSession, packet2, packet1, clientKeyPair.getPublicKey(), serverKeyPair);

        // Client: process the server packet and compute the session keys
        Hydrogen.HydroKxSessionKeyPair clientSession = new Hydrogen.HydroKxSessionKeyPair();
        hydrogen.hydro_kx_kk_3(stateClient, clientSession, packet2, clientKeyPair);

        assertTrue(encryptFromServerToClient(message, contextBytes, serverSession.getRx(), clientSession.getTx()));
    }


}
