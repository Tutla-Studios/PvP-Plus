package net.tutla.pvpPlus.commandSystem.command;

import net.tutla.pvpPlus.PvpPlus;
import net.tutla.pvpPlus.commandSystem.CommandContext;
import net.tutla.pvpPlus.commandSystem.CommandSection;
import net.tutla.pvpPlus.commandSystem.CommandTabAutoComplete;
import net.tutla.pvpPlus.commandSystem.TutlaCommand;
import net.tutla.pvpPlus.config.ConfigSetting;
import net.tutla.pvpPlus.config.PluginConfig;
import net.tutla.pvpPlus.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class PvpSettingsCommand extends TutlaCommand {

    public PvpSettingsCommand() {
        super("pvpsettings", "/pvpsettings <setting> <value>", "Configure plugin settings",
                CommandSection.SETTINGS,
                buildAutoComplete());
    }

    private static CommandTabAutoComplete buildAutoComplete() {
        List<CommandTabAutoComplete> children = new ArrayList<>();
        List<String> settingKeys = new ArrayList<>();

        for (ConfigSetting setting : ConfigSetting.values()) {
            settingKeys.add(setting.key);
            List<String> values = switch (setting.type) {
                case BOOLEAN -> List.of("true", "false");
                case LOCATION -> List.of("here", "clear");
            };
            children.add(
                new CommandTabAutoComplete(setting.key, List.of(), "<values>")
                    .setValues(values)
            );
        }

        return new CommandTabAutoComplete("pvpsettings", children, "<values>")
                .setValues(settingKeys);
    }

    @Override
    public boolean run(CommandContext ctx) {
        PluginConfig config = PvpPlus.getInstance().getPluginConfig();

        if (ctx.args.length == 0) {
            ctx.player.sendMessage(TextUtil.parse("<gold>--- PvP Settings ---"));
            for (ConfigSetting setting : ConfigSetting.values()) {
                ctx.player.sendMessage(TextUtil.parse(
                        "<white>" + setting.key +
                        " <dark_gray>(" + setting.type.name().toLowerCase() + ")" +
                        " <gray>= <yellow>" + config.describe(setting) +
                        "  <dark_gray>" + setting.description));
            }
            return true;
        }

        if (ctx.args.length < 2) return false;

        ConfigSetting setting = ConfigSetting.fromKey(ctx.args[0]);
        if (setting == null) {
            ctx.player.sendMessage(TextUtil.parse("<red>Unknown setting: <yellow>" + ctx.args[0]));
            return true;
        }

        switch (setting.type) {
            case LOCATION -> handleLocation(ctx, setting, config);
            case BOOLEAN -> handleBoolean(ctx, setting, config);
        }
        return true;
    }

    private void handleLocation(CommandContext ctx, ConfigSetting setting, PluginConfig config) {
        switch (ctx.args[1].toLowerCase()) {
            case "here" -> {
                config.setLocation(setting, ctx.player.getLocation());
                ctx.player.sendMessage(TextUtil.parse(
                        "<green>Set <yellow>" + setting.key + "</yellow> to " +
                        String.format("<white>%.1f, %.1f, %.1f</white> in <white>%s</white>.",
                                ctx.player.getLocation().getX(),
                                ctx.player.getLocation().getY(),
                                ctx.player.getLocation().getZ(),
                                ctx.player.getWorld().getName())));
            }
            case "clear" -> {
                config.clearSetting(setting);
                ctx.player.sendMessage(TextUtil.parse("<yellow>Cleared <white>" + setting.key + "</white>."));
            }
            default -> ctx.player.sendMessage(TextUtil.parse(
                    "<red>Use <yellow>here</yellow> to set to your position, or <yellow>clear</yellow> to unset."));
        }
    }

    private void handleBoolean(CommandContext ctx, ConfigSetting setting, PluginConfig config) {
        String val = ctx.args[1].toLowerCase();
        if (!val.equals("true") && !val.equals("false")) {
            ctx.player.sendMessage(TextUtil.parse("<red>Use <yellow>true</yellow> or <yellow>false</yellow>."));
            return;
        }
        boolean boolValue = val.equals("true");
        config.setBoolean(setting, boolValue);
        ctx.player.sendMessage(TextUtil.parse(
                "<green>Set <yellow>" + setting.key + "</yellow> to <white>" + boolValue + "</white>."));
    }
}
