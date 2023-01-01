package me.cjcrafter.simpleworlds;

import me.deecaad.core.commands.*;
import me.deecaad.core.commands.arguments.LocationArgumentType;
import me.deecaad.core.commands.arguments.PlayerArgumentType;
import me.deecaad.core.commands.arguments.StringArgumentType;
import me.deecaad.core.lib.adventure.text.format.Style;
import me.deecaad.core.lib.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class Command {

    public static final Function<CommandData, Tooltip[]> WORLDS = (data) -> Bukkit.getWorlds().stream().map(World::getName).map(Tooltip::of).toArray(Tooltip[]::new);

    public static void register() {
        CommandBuilder cmd = new CommandBuilder("sw")
                .withPermission("simpleworlds.admin")
                .withAliases("simpleworlds", "simpleworld")
                .withDescription("SimpleWorlds' main command")
                .withSubcommand(new CommandBuilder("tp")
                        .withPermission("simpleworlds.commands.tp")
                        .withAliases("teleport")
                        .withDescription("Teleport a player to a different world")
                        .withArgument(new Argument<>("world", new StringArgumentType()).withDesc("Which world to teleport to").replace(WORLDS))
                        .withArgument(new Argument<>("player", new PlayerArgumentType(), null).withDesc("Which player to teleport (defaults to you)"))
                        .withArgument(new Argument<>("location", new LocationArgumentType(), null).withDesc("Where to teleport the player"))
                        .executes(CommandExecutor.any((sender, args) -> {
                            World world = Bukkit.getWorld((String) args[0]);
                            Player player = (Player) args[1];
                            Location location = (Location) args[2];

                            if (world == null) {
                                sender.sendMessage(ChatColor.RED + "Unknown world '" + args[0] + "'");
                                return;
                            }

                            if (player == null) {
                                if (sender instanceof Player p1)
                                    player = p1;

                                if (player == null) {
                                    sender.sendMessage(ChatColor.RED + "Could not determine player");
                                    return;
                                }
                            }

                            if (location == null) {
                                location = world.getSpawnLocation();
                            }

                            location.setWorld(world);
                            player.teleport(location);
                        })))

                .withSubcommand(new CommandBuilder("import")
                        .withPermission("simpleworlds.commands.import")
                        .withDescription("Imports a world from a file, or creates a new world")
                        .withArgument(new Argument<>("world", new StringArgumentType()))
                        .executes(CommandExecutor.any((sender, args) -> {
                            if (Bukkit.getWorld((String) args[0]) != null) {
                                sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' already exists! Try a different name.");
                                return;
                            }

                            new WorldCreator((String) args[0]).createWorld();
                            SimpleWorlds.getPlugin().addWorld((String) args[0]);
                        })));

        Style highlight = Style.style(TextColor.color(159, 43, 104));
        Style accent = Style.style(TextColor.color(242, 210, 189));
        cmd.registerHelp(new HelpCommandBuilder.HelpColor(highlight, accent, "\u27A2"));
        cmd.register();
    }
}
