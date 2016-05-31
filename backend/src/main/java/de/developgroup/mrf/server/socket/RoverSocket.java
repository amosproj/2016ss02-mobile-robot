package de.developgroup.mrf.server.socket;

import java.io.IOException;

import de.developgroup.mrf.server.handler.DeveloperSettingsHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.developgroup.mrf.server.ClientManager;
import de.developgroup.mrf.server.handler.RoverHandler;
import de.developgroup.mrf.server.rpc.JsonRpc2Socket;

public class RoverSocket extends JsonRpc2Socket {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RoverSocket.class);

	@Inject
	public static RoverHandler roverHandler;

	@Inject
	public static DeveloperSettingsHandler developerSettingsHandler;

	@Inject
	private static ClientManager clientManager;

	public RoverSocket() {
	}

	@Override
	public void onWebSocketConnect(final Session sess) {
		super.onWebSocketConnect(sess);
		clientManager.addClient(sess);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		clientManager.removeClosedSessions();
		if (clientManager.isNoClientConnected()) {
			try {
				roverHandler.stop();
			} catch (IOException e) {
				LOGGER.info("stop movement error detected ", e);
			}
		}
	}

	public String ping(Number sqn) {
		LOGGER.trace("ping({})", sqn);
		return roverHandler.handlePing(sqn.intValue());
	}

	public void driveForward(Number desiredSpeed) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("driveForeward({})", desiredSpeed);
		roverHandler.driveForward(desiredSpeed.intValue());
	}

	public void driveBackward(Number desiredSpeed) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("driveBackward({})", desiredSpeed);
		roverHandler.driveBackward(desiredSpeed.intValue());
	}

	public void stop() throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("stop()");
		roverHandler.stop();
	}

	public void turnLeft(Number turnRate) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnLeft({})", turnRate);
		roverHandler.turnLeft(turnRate.intValue());
	}

	public void turnRight(Number turnRate) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnRight({})", turnRate);
		roverHandler.turnRight(turnRate.intValue());
	}

	public void turnHeadUp(Number angle) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnHeadUp({})", angle);
		roverHandler.turnHeadUp(angle.intValue());
	}

	public void turnHeadDown(Number angle) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnHeadDown({})", angle);
		roverHandler.turnHeadDown(angle.intValue());
	}

	public void turnHeadLeft(Number angle) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnHeadLeft({})", angle);
		roverHandler.turnHeadLeft(angle.intValue());
	}

	public void turnHeadRight(Number angle) throws IOException {
		if(developerSettingsHandler.checkKillswitchEnabled()){
			return;
		}
		LOGGER.trace("turnHeadRight({})", angle);
		roverHandler.turnHeadRight(angle.intValue());
	}

	public void resetHeadPosition() throws IOException {
		if (developerSettingsHandler.checkKillswitchEnabled()) {
			return;
		}
		LOGGER.trace("resetHeadPosition()");
		roverHandler.resetHeadPosition();
	}

	public void setKillswitch(Boolean killswitchEnabled) throws IOException {
		developerSettingsHandler.setKillswitchEnabled(killswitchEnabled);
	}

	// TODO: Delete if not needed
	public Boolean getKillswitchState(){
		return developerSettingsHandler.isKillswitchEnabled();
	}

	public void sendKillswitchState(){
		developerSettingsHandler.informClients();
	}
}
