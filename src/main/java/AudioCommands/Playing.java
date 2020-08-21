package AudioCommands;


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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

@CommandInfo(
        name = "skip",
        description = "Play Music from Links or Search Terms!",
        requirements = "The Bot needs permission to join VC"
)

@RequiredPermissions(Permission.VOICE_CONNECT)

@Author("Rin#6969")
public class Playing extends Command {



    private AudioPlayerManager playerManager;


    public Playing() {
        this.name = "playing";
        this.help = "get playing tracke";
        this.arguments = "No further Arguments";
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
        this.guildOnly = false;
        this.ownerCommand = false;

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }


    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        MusicManager manager = getGuildAudioPlayer(event.getGuild());

        if (!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect() || manager.player.isPaused() || manager.player.getPlayingTrack() == null) {
            VoiceChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("ERROR");
            builder.setColor(Color.red);
            builder.addField("Failed to load info", "Reason: Bot isn't playing anything!", false);


            event.getChannel().sendMessage(builder.build()).queue();

        } else {
            AudioTrack track = manager.player.getPlayingTrack();
            String title = manager.player.getPlayingTrack().getInfo().title;
            String duration = formatTiming(manager.player.getPlayingTrack().getDuration(), manager.player.getPlayingTrack().getDuration());
            String position = formatTiming(manager.player.getPlayingTrack().getPosition(), manager.player.getPlayingTrack().getDuration());
            String thumbnail = "https://img.youtube.com/vi/"+manager.player.getPlayingTrack().getIdentifier()+"/mqdefault.jpg";
            String link = manager.player.getPlayingTrack().getInfo().uri;

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Now playing")
                    .setAuthor("Track by: "+track.getInfo().author);
            builder.addField("Title", "["+title+"]"+"("+link+")", true);
            builder.addField("Duration", duration, true);
            builder.addField("Timestamp", position + "/" + duration, true);
            builder.setImage(thumbnail);
            event.getChannel().sendMessage(builder.build()).queue();

        }





    }

    private static String formatTiming(long timing, long maximum) {
        timing = Math.min(timing, maximum) / 1000;

        long seconds = timing % 60;
        timing /= 60;
        long minutes = timing % 60;
        timing /= 60;
        long hours = timing;

        if (maximum >= 3600000L) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = Play.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            Play.musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void skipTrack(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

}
