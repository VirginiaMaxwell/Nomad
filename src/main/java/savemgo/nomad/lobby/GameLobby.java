package savemgo.nomad.lobby;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import savemgo.nomad.NomadLobby;
import savemgo.nomad.entity.Lobby;
import savemgo.nomad.helper.Characters;
import savemgo.nomad.helper.Chat;
import savemgo.nomad.helper.Games;
import savemgo.nomad.helper.Hosts;
import savemgo.nomad.helper.Hub;
import savemgo.nomad.helper.Mail;
import savemgo.nomad.helper.Users;
import savemgo.nomad.packet.Packet;
import savemgo.nomad.util.Packets;

@Sharable
public class GameLobby extends NomadLobby {

	private static final Logger logger = LogManager.getLogger(GameLobby.class);

	public GameLobby(Lobby lobby) {
		super(lobby);
	}

	@Override
	public boolean handlePacket(ChannelHandlerContext ctx, Packet in) {
		int command = in.getCommand();

		switch (command) {

		/** Accounts */
		case 0x3003:
			Users.checkSession(ctx, in, getLobby(), true);
			break;

		/** Characters */
		case 0x4100:
			// Get Profile Data
			Characters.getCharacterInfo(ctx);
			Characters.getGameplayOptionsUiSettings(ctx);
			Characters.getChatMacros(ctx);
			Characters.getPersonalInfo(ctx);
			Characters.getGear(ctx);
			Characters.getSkills(ctx);
			Characters.getSkillSets(ctx);
			Characters.getGearSets(ctx);
			break;

		case 0x4102:
			Characters.getPersonalStats(ctx, in);
			break;

		case 0x4110:
			Characters.updateGameplayOptions(ctx, in);
			break;

		case 0x4112:
			Characters.updateUiSettings(ctx, in);
			break;

		case 0x4114:
			Characters.updateChatMacros(ctx, in);
			break;

		case 0x4130:
			Characters.updatePersonalInfo(ctx, in);
			break;

		case 0x4141:
			Characters.updateSkillSets(ctx, in);
			break;

		case 0x4143:
			Characters.updateGearSets(ctx, in);
			break;

		case 0x4220:
			Characters.getCharacterCard(ctx, in);
			break;

		case 0x4500:
			Characters.addFriendsBlocked(ctx, in);
			break;

		case 0x4510:
			Characters.removeFriendsBlocked(ctx, in);
			break;

		case 0x4580:
			Characters.getFriendsBlockedList(ctx, in);
			break;

		case 0x4600:
			Characters.search(ctx, in);
			break;

		case 0x4680:
			Characters.getMatchHistory(ctx, in);
			break;

		case 0x4684:
			Characters.getOfficialGameHistory(ctx, in);
			break;

		case 0x4700:
			Characters.updateConnectionInfo(ctx, in);
			break;

		/** Mail */
		case 0x4820:
			Mail.getMail(ctx, in);
			break;

		case 0x4840:
			Mail.getContents(ctx, in);
			break;

		/** Games */
		case 0x4300:
			Games.getList(ctx, getLobby(), 0x4301);
			// Games.getListFile(ctx, getId(), 0x4301);
			break;

		case 0x4312:
			Games.getDetails(ctx, in, getLobby());
			// Games.getDetailsFile(ctx, in, getId());
			break;

		case 0x4320:
			Games.join(ctx, in);
			// Games.joinHostFile(ctx, in);
			break;

		case 0x4322:
			Games.joinFailed(ctx, in);
			break;

		/** Host */
		case 0x4304:
			Hosts.getSettings(ctx, getLobby());
			break;

		case 0x4310:
			Hosts.updateSettings(ctx, in, getLobby());
			break;

		case 0x4316:
			Hosts.createGame(ctx, getLobby());
			break;

		case 0x4340:
			Hosts.playerConnected(ctx, in);
			break;

		case 0x4342:
			Hosts.playerDisconnected(ctx, in);
			break;

		case 0x4344:
			Hosts.setPlayerTeam(ctx, in);
			break;

		case 0x4346:
			Hosts.kickPlayer(ctx, in);
			break;

		case 0x4380:
			Hosts.quitGame(ctx);
			break;

		case 0x4390:
			// Stats
			Packets.write(ctx, 0x4391);
			break;

		case 0x4394:
			// updategameenv
			logger.error("Update Game Environment not implemented.");
			break;

		case 0x4392:
			Hosts.setGame(ctx, in);
			break;

		case 0x4398:
			Hosts.updatePings(ctx, in);
			break;

		case 0x43a0:
			Hosts.pass(ctx, in);
			break;

		case 0x43a2:
			// Unknown, end of round, stats?
			Packets.write(ctx, 0x43a3, 0);
			break;

		case 0x43c0:
			// At start of hosted Training game, after Team join
			// Perhaps in-game information update?
			Packets.write(ctx, 0x43c1);
			break;

		case 0x43ca:
			// Start Round
			Packets.write(ctx, 0x43cb, 0);
			break;

		/** Players */
		case 0x4400:
			Chat.send(ctx, in);
			break;

		case 0x4440:
			// Set Team
			Packets.write(ctx, 0x4441, 0);
			break;

		/** Hub */
		case 0x4150:
			Hub.onLobbyDisconnect(ctx, in);
			break;

		case 0x43d0:
			Hub.onTrainingConnect(ctx, in);
			break;

		case 0x4900:
			Hub.getGameLobbyInfo(ctx);
			break;

		case 0x4990:
			Hub.getGameEntryInfo(ctx);
			break;

		// case 0x4992:
		// When prompted to re-connect to a game, cancelled
		// In: ff ff ff ff, game id?

		// case 0x4914:
		// When prompted to re-connect to a game, cancelled

		/** Unknown */
		case 0x4128:
			// Player still here after a kicked host passed
			Packets.write(ctx, 0x4129, 0);
			break;

		default:
			logger.error("Couldn't handle command " + Integer.toHexString(in.getCommand()));
			return false;
		}

		return true;
	}

	@Override
	public void onChannelInactive(ChannelHandlerContext ctx) {
		Users.onLobbyDisconnected(ctx, getLobby());
	}

}
