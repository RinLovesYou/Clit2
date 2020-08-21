package AudioCommands;


import Audio.AudioLoadResultHandler;
import Audio.MusicManager;
import Main.main;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.AudioManager;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@CommandInfo(
        name = "play",
        description = "Play Music from Links or Search Terms!",
        requirements = "The Bot needs permission to join VC"
)

@RequiredPermissions(Permission.VOICE_CONNECT)

@Author("Rin#6969")
public class Play extends Command {



    private static AudioPlayerManager playerManager;
    public static Map<Long, MusicManager> musicManagers;
    public static BlockingQueue<AudioTrack> queue;


    public Play() {
        this.name = "play";
        this.help = "Plays some Music either through a Link or a Search term!";
        this.arguments = "link or search query";
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
        this.guildOnly = false;
        this.ownerCommand = false;
        musicManagers = new HashMap<>();
        queue = new LinkedBlockingQueue<>();

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }


    @Override
    protected void execute(CommandEvent event) {


        Guild guild = event.getGuild();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if (!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect()) {
            VoiceChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();

            event.getGuild().getAudioManager().openAudioConnection(voiceChannel);

        }

        String url = null;




        if(message.startsWith("!7play https://")) {

            url = message.replaceFirst("!7play ", "");
        }
        else {
            url = "ytsearch: " + message.replaceFirst("!7play ", "");
        }

        loadAndPlay(event.getTextChannel(), url);



    }

    public static synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public static void loadAndPlay(final TextChannel channel, final String trackUrl) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler handler = new AudioLoadResultHandler(musicManager.player, musicManager);
        connectToFirstVoiceChannel(channel.getGuild().getAudioManager());
        playerManager.loadItemOrdered(musicManager, trackUrl, handler);
    }

    private void play(Guild guild, MusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannels().get(0));
        }
    }
}
