/**
 * Mirai Song Plugin
 * Copyright (C) 2021  khjxiaogu
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.khjxiaogu.MiraiSongPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.google.gson.*;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.AmrVoiceProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.LightAppXCardProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.MiraiMusicCard;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.PlainMusicInfoProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.ShareCardProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.SilkVoiceProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.XMLCardProvider;
import com.khjxiaogu.MiraiSongPlugin.musicsource.BaiduMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.BiliBiliMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.KugouMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.LocalFileSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseAdvancedRadio;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseHQMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseRadioSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicHQSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.XimalayaSource;
import com.khjxiaogu.MiraiSongPlugin.permission.GlobalMatcher;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import net.mamoe.yamlkt.Yaml;
import net.mamoe.yamlkt.YamlElement;
import net.mamoe.yamlkt.YamlLiteral;
import net.mamoe.yamlkt.YamlMap;

// TODO: Auto-generated Javadoc

/**
 * ????????????
 *
 * @author khjxiaogu
 * file: MiraiSongPlugin.java
 * time: 2020???8???26???
 */
public class MiraiSongPlugin extends JavaPlugin {
    public MiraiSongPlugin() {
        super(new JvmPluginDescriptionBuilder(PluginData.id, PluginData.ver).name(PluginData.name)
                .author(PluginData.auth).info(PluginData.info).build());
    }

    //??????????????????
    public static final String UserConfPath = "/home/mirai/Dice3349795206/UserConfDir/";

    //??????????????????
    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    //????????????????????????3000
    public boolean judgeFavor(String filename) {
        String jsonStr = readToString(filename);
        if (jsonStr == null) {
            return false;
        }
        try {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            JsonElement favorElement = gs.fromJson(jsonStr, JsonObject.class).get("?????????");
            if (favorElement != null) {
                int favor = favorElement.getAsInt();
                return favor >= 1000;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private final String spliter = " ";
    List<Long> admins = new ArrayList<>();
    // ???????????????????????????
    private Executor exec = Executors.newFixedThreadPool(8);

    /**
     * ????????????.
     */
    public static final Map<String, BiConsumer<MessageEvent, String[]>> commands = new ConcurrentHashMap<>();

    /**
     * ????????????.
     */
    public static final Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * ????????????
     */
    public static final Map<String, MusicCardProvider> cards = new ConcurrentHashMap<>();

    static {
        // ??????????????????
        sources.put("QQ??????", new QQMusicSource());
        sources.put("QQ??????HQ", new QQMusicHQSource());
        sources.put("??????", new NetEaseMusicSource());
        sources.put("??????????????????", new NetEaseAdvancedRadio());
        sources.put("????????????", new NetEaseRadioSource());
        sources.put("??????HQ", new NetEaseHQMusicSource());
        sources.put("??????", new KugouMusicSource());
        sources.put("??????", new BaiduMusicSource());
        sources.put("Bilibili", new BiliBiliMusicSource());
        sources.put("????????????", new XimalayaSource());
        sources.put("??????", new LocalFileSource());
        // ????????????
        // cards.put("LightApp", new LightAppCardProvider());
        cards.put("LightApp", new MiraiMusicCard());
        cards.put("LightAppX", new LightAppXCardProvider());
        cards.put("XML", new XMLCardProvider());
        cards.put("Silk", new SilkVoiceProvider());
        cards.put("AMR", new AmrVoiceProvider());
        cards.put("Share", new ShareCardProvider());
        cards.put("Message", new PlainMusicInfoProvider());
        cards.put("Mirai", new MiraiMusicCard());
    }

    static {
        HttpURLConnection.setFollowRedirects(true);
    }

    private static MiraiSongPlugin plugin;

    public static MiraiLogger getMLogger() {
        return plugin.getLogger();
    }

    GlobalMatcher matcher = new GlobalMatcher();
    String unfoundSong;
    String unavailableShare;
    String templateNotFound;
    String sourceNotFound;

    /**
     * ???????????????????????????????????????????????????
     *
     * @param source ??????????????????
     * @param card   ??????????????????
     * @return return ???????????????????????????????????????????????????????????????
     */
    public BiConsumer<MessageEvent, String[]> makeTemplate(String source, String card) {
        if (source.equals("all"))
            return makeSearchesTemplate(card);
        MusicCardProvider cb = cards.get(card);
        if (cb == null)
            throw new IllegalArgumentException("card template not exists");
        MusicSource mc = sources.get(source);
        if (mc == null)
            throw new IllegalArgumentException("music source not exists");
        return (event, args) -> {
            String sn;
            try {
                sn = URLEncoder.encode(String.join(spliter, Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
                return;
            }
            exec.execute(() -> {
                MusicInfo mi;
                try {
                    mi = mc.get(sn);
                } catch (Throwable t) {
                    this.getLogger().debug(t);
                    Utils.getRealSender(event).sendMessage(unfoundSong);
                    return;
                }
                try {
                    Utils.getRealSender(event).sendMessage(cb.process(mi, Utils.getRealSender(event)));
                } catch (Throwable t) {
                    this.getLogger().debug(t);
                    // this.getLogger().
                    Utils.getRealSender(event).sendMessage(unavailableShare);
                    return;
                }
            });
        };
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param card ??????????????????
     * @return return ???????????????????????????????????????????????????????????????
     */
    public BiConsumer<MessageEvent, String[]> makeSearchesTemplate(String card) {
        MusicCardProvider cb = cards.get(card);
        if (cb == null)
            throw new IllegalArgumentException("card template not exists");
        return (event, args) -> {
            String sn;
            try {
                sn = URLEncoder.encode(String.join(spliter, Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
                return;
            }
            exec.execute(() -> {
                for (MusicSource mc : sources.values()) {
                    if (!mc.isVisible())
                        continue;
                    MusicInfo mi;
                    try {
                        mi = mc.get(sn);
                    } catch (Throwable t) {
                        this.getLogger().debug(t);
                        continue;
                    }
                    try {
                        Utils.getRealSender(event).sendMessage(cb.process(mi, Utils.getRealSender(event)));
                    } catch (Throwable t) {
                        this.getLogger().debug(t);
                        Utils.getRealSender(event).sendMessage(unavailableShare);
                    }
                    return;
                }
                Utils.getRealSender(event).sendMessage(unfoundSong);
            });

        };
    }

    @SuppressWarnings("resource")
    @Override
    public void onEnable() {
        plugin = this;
        if (!new File(this.getDataFolder(), "global.permission").exists()) {
            try (FileOutputStream fos = new FileOutputStream(new File(this.getDataFolder(), "global.permission"))) {
                fos.write("#global fallback permission file".getBytes());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            try {
                new FileOutputStream(new File(this.getDataFolder(), "config.yml"))
                        .write(Utils.readAll(this.getResourceAsStream("config.yml")));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        File local = new File("SongPluginLocal.json");
        if (!local.exists()) {
            try {
                Gson gs = new GsonBuilder().setPrettyPrinting().create();
                local.createNewFile();
                JsonArray datas = new JsonArray();
                JsonObject obj = new JsonObject();
                obj.addProperty("title", "??????");
                obj.addProperty("desc", "?????????");
                obj.addProperty("previewUrl", "????????????url");
                obj.addProperty("musicUrl", "????????????url");
                obj.addProperty("jumpUrl", "????????????url");
                obj.addProperty("source", "??????");
                datas.add(obj);
                try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(local), "UTF-8")) {
                    gs.toJson(datas, fw);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        reload();
        GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost(this.getCoroutineContext()) {
            @EventHandler
            public void onGroup(GroupMessageEvent event) {

                String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
                BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
                if (exec != null)
                    //???????????????????????????1000
                    if (judgeFavor(UserConfPath + event.getSender().getId() + "/favorConf.json")) {
                        if (matcher.match(event.getSender()).isAllowed())
                            exec.accept(event, args);

                    } else {
                        event.getGroup().sendMessage("??????????????????????????????????????????????????1000???~");
                    }
            }

            @EventHandler
            public void onFriend(FriendMessageEvent event) {
                String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
                BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
                if (exec != null)
                    //????????????????????????1000
                    if (judgeFavor(UserConfPath + event.getSender().getId() + "/favorConf.json")) {
                        if (matcher.match(event.getSender(), false).isAllowed())
                            exec.accept(event, args);

                    } else {
                        event.getSender().sendMessage("??????????????????????????????????????????????????1000???~");
                    }
            }
        });
        getLogger().info("??????????????????!");
    }

    public void reload() {
        YamlMap cfg = Yaml.Default.decodeYamlMapFromString(
                new String(Utils.readAll(new File(this.getDataFolder(), "config.yml")), StandardCharsets.UTF_8));
        matcher.load(this.getDataFolder());
        YamlMap excs = (YamlMap) cfg.get(new YamlLiteral("extracommands"));
        String addDefault = cfg.getStringOrNull("adddefault");
        try {
            List<Object> adms = cfg.getList("admins");
            if (adms != null)
                for (Object o : adms) {
                    try {
                        admins.add(Long.parseLong(String.valueOf(o)));
                    } catch (Exception ex) {
                        this.getLogger().warning(ex);
                    }
                }
        } catch (Exception ex) {
            this.getLogger().warning("???????????????????????????????????????????????????");
        }

        commands.clear();
        if (addDefault == null || addDefault.equals("true")) {
            commands.put("/??????", makeSearchesTemplate("Mirai"));
            commands.put("/??????", makeSearchesTemplate("Message"));
//            commands.put("#??????", makeSearchesTemplate("AMR"));
            commands.put("/QQ", makeTemplate("QQ??????", "Mirai"));// ????????????
            commands.put("/??????", makeTemplate("??????", "Mirai"));
            commands.put("/????????????", makeTemplate("??????????????????", "Mirai"));
            commands.put("/??????", makeTemplate("??????", "Mirai"));
            commands.put("/??????", makeTemplate("??????", "XML"));
//            commands.put("/??????", (event, args) -> {
//                String sn;
//                try {
//                    sn = URLEncoder.encode(String.join(spliter, Arrays.copyOfRange(args, 3, args.length)), "UTF-8");
//                } catch (UnsupportedEncodingException ignored) {
//                    return;
//                }
//                exec.execute(() -> {
//                    try {
//                        MusicSource ms = sources.get(args[1]);
//                        if (ms == null) {
//                            Utils.getRealSender(event).sendMessage("???????????????");
//                            return;
//                        }
//                        MusicCardProvider mcp = cards.get(args[2]);
//                        if (mcp == null) {
//                            Utils.getRealSender(event).sendMessage("??????????????????");
//                            return;
//                        }
//                        MusicInfo mi;
//                        try {
//                            mi = ms.get(sn);
//                        } catch (Throwable t) {
//                            this.getLogger().debug(t);
//                            Utils.getRealSender(event).sendMessage(unfoundSong);
//                            return;
//                        }
//                        try {
//                            Utils.getRealSender(event).sendMessage(mcp.process(mi, Utils.getRealSender(event)));
//                        } catch (Throwable t) {
//                            this.getLogger().debug(t);
//                            Utils.getRealSender(event).sendMessage(unavailableShare);
//                            return;
//                        }
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                        Utils.getRealSender(event).sendMessage(unfoundSong);
//                    }
//                });
//            });
        }
        if (excs != null)
            for (YamlElement cmd : excs.getKeys()) {
                commands.put(cmd.toString(), makeTemplate(((YamlMap) excs.get(cmd)).getString("source"),
                        ((YamlMap) excs.get(cmd)).getString("card")));
            }
        commands.put("/msp", (ev, args) -> {
            if (!admins.contains(ev.getSender().getId()))
                return;
            if (args[1].equals("reload")) {
                reload();
                ev.getSender().sendMessage("???????????????");
            } else if (args[1].equals("setperm")) {
                try {
                    matcher.loadString(args[2], ev.getBot());
                    ev.getSender().sendMessage("???????????????????????????");
                } catch (Exception ex) {
                    ev.getSender().sendMessage("???????????????????????????");
                    getLogger().warning(ex);
                }
            } else if (args[1].equals("setgperm")) {
                try {
                    matcher.loadString(args[2]);
                    ev.getSender().sendMessage("???????????????????????????");
                } catch (Exception ex) {
                    ev.getSender().sendMessage("???????????????????????????");
                    getLogger().warning(ex);
                }
            } else if (args[1].equals("buildperm")) {
                try {
                    matcher.rebuildConfig();
                    ev.getSender().sendMessage("?????????????????????");
                } catch (Exception ex) {
                    ev.getSender().sendMessage("?????????????????????");
                    getLogger().warning(ex);
                }
            }
        });
        AmrVoiceProvider.ffmpeg = SilkVoiceProvider.ffmpeg = cfg.getString("ffmpeg_path");
        String amras = cfg.getStringOrNull("amrqualityshift");
        String amrwb = cfg.getStringOrNull("amrwb");
        String usecc = cfg.getStringOrNull("use_custom_ffmpeg_command");
        String ulocal = cfg.getStringOrNull("enable_local");
        String vb = cfg.getStringOrNull("verbose");
        unfoundSong = cfg.getStringOrNull("hintsongnotfound");
        if (unfoundSong == null)
            unfoundSong = "?????????????????????";
        unavailableShare = cfg.getStringOrNull("hintcarderror");
        if (unavailableShare == null)
            unavailableShare = "?????????????????????";
        templateNotFound = cfg.getStringOrNull("hintnotemplate");
        if (templateNotFound == null)
            templateNotFound = "?????????????????????";
        sourceNotFound = cfg.getStringOrNull("hintsourcenotfound");
        if (sourceNotFound == null)
            sourceNotFound = "?????????????????????";
        LocalFileSource.autoLocal = ulocal != null && ulocal.equals("true");
        AmrVoiceProvider.autoSize = amras != null && amras.equals("true");
        AmrVoiceProvider.wideBrand = amrwb == null || amrwb.equals("true");
        AmrVoiceProvider.customCommand = (usecc != null && usecc.equals("true"))
                ? cfg.getStringOrNull("custom_ffmpeg_command")
                : null;
        Utils.verbose = vb == null || vb.equals("true");
        SilkVoiceProvider.silk = cfg.getString("silkenc_path");
        if (AmrVoiceProvider.customCommand == null) {
            try {
                Utils.exeCmd(AmrVoiceProvider.ffmpeg, "-version");
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                getLogger().warning("ffmpeg????????????????????????????????????");
            }
            getLogger().info("????????????????????????AMR:" + AmrVoiceProvider.wideBrand + " AMR????????????:" + AmrVoiceProvider.autoSize);
        } else
            getLogger().info("?????????????????????????????????:" + AmrVoiceProvider.customCommand);
    }
}
