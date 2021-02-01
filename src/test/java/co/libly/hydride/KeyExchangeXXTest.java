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

public class KeyExchangeXXTest extends BaseTest {

    private String context = "context1";
    private byte[] contextBytes = context.getBytes();

    private String message = "This is a message that will be encrypted.";
    private byte[] messageBytes = message.getBytes();

    @Test
    public void keyExchange() {
        // Generate server and client long-term keypairs
        Hydrogen2.HydroKxKeyPair serverKeyPair = new Hydrogen2.HydroKxKeyPair();
        Hydrogen2.HydroKxKeyPair clientKeyPair = new Hydrogen2.HydroKxKeyPair();
        hydrogen.hydro_kx_keygen(serverKeyPair);
        hydrogen.hydro_kx_keygen(clientKeyPair);

        // Client: Initiate a key exchange
        byte[] packet1 = new byte[Hydrogen2.HYDRO_KX_XX_PACKET1BYTES];
        Hydrogen2.HydroKxState stateClient = new Hydrogen2.HydroKxState();
        hydrogen.hydro_kx_xx_1(stateClient, packet1, null);

        // Server: process the initial request from the client, and compute the session keys
        byte[] packet2 = new byte[Hydrogen2.HYDRO_KX_XX_PACKET2BYTES];
        Hydrogen2.HydroKxState stateServer = new Hydrogen2.HydroKxState();
        Hydrogen2.HydroKxSessionKeyPair serverSession = new Hydrogen2.HydroKxSessionKeyPair();
        hydrogen.hydro_kx_xx_2(stateServer, packet2, packet1, null, serverKeyPair);

        // Client: process the server packet and compute the session keys
        byte[] packet3 = new byte[Hydrogen2.HYDRO_KX_XX_PACKET3BYTES];
        Hydrogen2.HydroKxSessionKeyPair clientSession = new Hydrogen2.HydroKxSessionKeyPair();
        hydrogen.hydro_kx_xx_3(stateClient, clientSession, packet3, null, packet2, null, clientKeyPair);

        // Server: process the client packet and compute the session keys:
        hydrogen.hydro_kx_xx_4(stateServer, serverSession, null, packet3, null);


        assertTrue(encryptFromServerToClient(message, contextBytes, serverSession.getTx(), clientSession.getRx()));
    }


}
