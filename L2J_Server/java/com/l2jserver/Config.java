/*
 * Copyright (C) 2004-2015 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.util.FloodProtectorConfig;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.PropertiesParser;
import com.l2jserver.util.StringUtil;
import com.l2jserver.util.data.xml.IXmlReader;

/**
 * This class loads all the game server related configurations from files.<br>
 * The files are usually located in config folder in server root folder.<br>
 * Each configuration has a default value (that should reflect retail behavior).
 */
public final class Config
{
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	
	// --------------------------------------------------
	// Constants
	// --------------------------------------------------
	public static final String EOL = System.lineSeparator();
	
	// --------------------------------------------------
	// L2J Property File Definitions
	// --------------------------------------------------
	public static final String CHARACTER_CONFIG_FILE = "./config/Character.properties";
	public static final String FEATURE_CONFIG_FILE = "./config/Feature.properties";
	public static final String FORTSIEGE_CONFIGURATION_FILE = "./config/FortSiege.properties";
	public static final String GENERAL_CONFIG_FILE = "./config/General.properties";
	public static final String HEXID_FILE = "./config/hexid.txt";
	public static final String ID_CONFIG_FILE = "./config/IdFactory.properties";
	public static final String L2JMOD_CONFIG_FILE = "./config/L2JMods.properties";
	public static final String LOGIN_CONFIGURATION_FILE = "./config/LoginServer.properties";
	public static final String NPC_CONFIG_FILE = "./config/NPC.properties";
	public static final String PVP_CONFIG_FILE = "./config/PVP.properties";
	public static final String RATES_CONFIG_FILE = "./config/Rates.properties";
	public static final String CONFIGURATION_FILE = "./config/Server.properties";
	public static final String IP_CONFIG_FILE = "./config/ipconfig.xml";
	public static final String SIEGE_CONFIGURATION_FILE = "./config/Siege.properties";
	public static final String TW_CONFIGURATION_FILE = "./config/TerritoryWar.properties";
	public static final String TELNET_FILE = "./config/Telnet.properties";
	public static final String FLOOD_PROTECTOR_FILE = "./config/FloodProtector.properties";
	public static final String MMO_CONFIG_FILE = "./config/MMO.properties";
	public static final String OLYMPIAD_CONFIG_FILE = "./config/Olympiad.properties";
	public static final String GRANDBOSS_CONFIG_FILE = "./config/GrandBoss.properties";
	public static final String GRACIASEEDS_CONFIG_FILE = "./config/GraciaSeeds.properties";
	public static final String CHAT_FILTER_FILE = "./config/chatfilter.txt";
	public static final String EMAIL_CONFIG_FILE = "./config/Email.properties";
	public static final String CH_SIEGE_FILE = "./config/ConquerableHallSiege.properties";
	public static final String GEODATA_FILE = "./config/GeoData.properties";
	public static final String CUSTOM_CONFIG_FILE = "./config/Custom.properties";
	public static final String CUSTOM_MESSAGE_FILE = "./config/CustomMessage.properties";
	
	// --------------------------------------------------
	// Zersia Custom Settings
	// --------------------------------------------------
	public static boolean ALLOW_HUMAN;
	public static boolean ALLOW_ELF;
	public static boolean ALLOW_DARKELF;
	public static boolean ALLOW_ORC;
	public static boolean ALLOW_DWARF;
	public static boolean ALLOW_KAMAEL;
	// --------------------------------------------------
	public static boolean ENABLE_CHAR_TITLE;
	public static String CHAR_TITLE;
	// ---------------------------------------------------
	public static boolean CUSTOM_STARTING_LOC;
	public static int CUSTOM_STARTING_LOC_X;
	public static int CUSTOM_STARTING_LOC_Y;
	public static int CUSTOM_STARTING_LOC_Z;
	// ---------------------------------------------------
	public static boolean RAIDBOSS_KILLED_ANNOUNCE;
	public static String RAIDBOSS_KILLED_ANNOUNCE_MSG;
	public static boolean GRANDBOSS_KILLED_ANNOUNCE;
	public static String GRANDBOSS_KILLED_ANNOUNCE_MSG;
	public static boolean ANNOUNCE_SPAWN_RAIDBOSS;
	public static String ANNOUNCE_SPAWN_RAIDBOSS_MSG;
	// ---------------------------------------------------
	public static boolean ENABLE_PC_BANG;
	public static int MAX_PC_BANG_POINTS;
	public static boolean ENABLE_DOUBLE_PC_BANG_POINTS;
	public static int DOUBLE_PC_BANG_POINTS_CHANCE;
	public static double PC_BANG_POINT_RATE;
	public static boolean RANDOM_PC_BANG_POINT;
	public static boolean PC_BANG_REWARD_LOW_EXP_KILLS;
	public static int PC_BANG_LOW_EXP_KILLS_CHANCE;
	// --------------------------------------------------
	public static int CUSTOM_STARTING_PC_BANG_POINT;
	public static int CUSTOM_STARTING_GAME_POINT;
	// --------------------------------------------------
	public static int PARTYGO_MINLEVEL;
	public static int PARTYGO_MAXLEVEL;
	public static int PARTYGO_ITEMID;
	public static int PARTYGO_ITEMCOUNT;
	public static boolean PARTYGO_ENABLE;
	// --------------------------------------------------
	public static boolean ANTIBOT_ENABLE;
	public static int ANTIBOT_LEVEL;
	public static int ANTIBOT_INPUTTIME;
	public static String ANTIBOT_MSG;
	// --------------------------------------------------
	public static int CHGSEX_ITEMID;
	public static int CHGSEX_ITEMCOUNT;
	public static boolean CHGSEX_ENABLE;
	// --------------------------------------------------
	public static boolean SELFENCHANT_ENABLE;
	public static int WEAPONMAX_ENCHANT;
	public static int WEAPON_ITEM;
	public static int WEAPON_ITEMCOUNT;
	public static int ARMORMAX_ENCHANT;
	public static int ARMOR_ITEM;
	public static int ARMOR_ITEMCOUNT;
	public static int JEWELSMAX_ENCHANT;
	public static int JEWELS_ITEM;
	public static int JEWELS_ITEMCOUNT;
	public static int BELTSHIRTMAX_ENCHANT;
	public static int BELTSHIRT_ITEM;
	public static int BELTSHIRT_ITEMCOUNT;
	// --------------------------------------------------
	public static boolean MAXENCHANT_ENABLE;
	public static int MAXWEAPONMAX_ENCHANT;
	public static int MAXWEAPON_ITEM;
	public static int MAXWEAPON_ITEMCOUNT;
	public static int MAXARMORMAX_ENCHANT;
	public static int MAXARMOR_ITEM;
	public static int MAXARMOR_ITEMCOUNT;
	public static int MAXJEWELSMAX_ENCHANT;
	public static int MAXJEWELS_ITEM;
	public static int MAXJEWELS_ITEMCOUNT;
	public static int MAXBELTSHIRTMAX_ENCHANT;
	public static int MAXBELTSHIRT_ITEM;
	public static int MAXBELTSHIRT_ITEMCOUNT;
	// --------------------------------------------------
	public static boolean LOTOENCHANT_ENABLE;
	public static int LOTOWEAPONMAX_ENCHANT;
	public static int LOTOWEAPON_ITEM;
	public static int LOTOWEAPON_ITEMCOUNT;
	public static int LOTOARMORMAX_ENCHANT;
	public static int LOTOARMOR_ITEM;
	public static int LOTOARMOR_ITEMCOUNT;
	public static int LOTOJEWELSMAX_ENCHANT;
	public static int LOTOJEWELS_ITEM;
	public static int LOTOJEWELS_ITEMCOUNT;
	public static int LOTOBELTSHIRTMAX_ENCHANT;
	public static int LOTOBELTSHIRT_ITEM;
	public static int LOTOBELTSHIRT_ITEMCOUNT;
	// --------------------------------------------------
	public static boolean EVENTENCHANT_ENABLE;
	public static int EVENT_ENCHANT;
	public static int EVENT_ENCHANTITEM;
	public static int EVENT_ENCHANTITEMCOUNT;
	// --------------------------------------------------
	public static boolean EVENTGAMBLE_ENABLE;
	public static int GAMBLERANKSET;
	public static int GAMBLEITEM;
	public static int GAMBLEITEMCOUNT;
	public static int GAMBLERANKITEM01;
	public static int GAMBLERANKITEMCOUNT01;
	public static int GAMBLERANKITEM02;
	public static int GAMBLERANKITEMCOUNT02;
	public static int GAMBLERANKITEM03;
	public static int GAMBLERANKITEMCOUNT03;
	public static int GAMBLERANKITEM04;
	public static int GAMBLERANKITEMCOUNT04;
	public static int GAMBLERANKITEM05;
	public static int GAMBLERANKITEMCOUNT05;
	public static int GAMBLERANKITEM06;
	public static int GAMBLERANKITEMCOUNT06;
	public static int GAMBLERANKITEM07;
	public static int GAMBLERANKITEMCOUNT07;
	public static int GAMBLERANKITEM08;
	public static int GAMBLERANKITEMCOUNT08;
	public static int GAMBLERANKITEM09;
	public static int GAMBLERANKITEMCOUNT09;
	public static int GAMBLERANKITEM10;
	public static int GAMBLERANKITEMCOUNT10;
	// --------------------------------------------------
	public static boolean GMGIFT_ENABLE;
	public static int GMGIFT_ITEMID;
	public static int GMGIFT_ITEMCOUNT;
	public static int GMGIFT_RUNNING_TIME;
	public static String GMGIFT_MSG;
	public static String ONLINEUSER_MSG;
	// --------------------------------------------------
	public static float SATURDAY_RATE_XP;
	public static float SATURDAY_RATE_SP;
	public static float WEEK_RATE_CODE1;
	public static float WEEK_RATE_CODE2;
	// --------------------------------------------------
	public static int CLANPOINT;
	public static String CLANPOINT_MSG;
	// --------------------------------------------------
	public static int GAMEPOINT;
	public static String GAMEPOINT_MSG;
	// --------------------------------------------------
	public static String ZERSIA_CONFIG;
	public static String ZERSIA_LICENCE;
	// --------------------------------------------------
	public static int SOLOINSTANCE_ITEMID1;
	public static int SOLOINSTANCE_ITEMCNT1;
	public static int SOLOINSTANCE_ITEMID2;
	public static int SOLOINSTANCE_ITEMCNT2;
	public static int SOLOINSTANCE_ITEMID3;
	public static int SOLOINSTANCE_ITEMCNT3;
	public static int SOLOINSTANCE_ITEMID4;
	public static int SOLOINSTANCE_ITEMCNT4;
	public static int SOLOINSTANCE_EXITING_LOC_X;
	public static int SOLOINSTANCE_EXITING_LOC_Y;
	public static int SOLOINSTANCE_EXITING_LOC_Z;
	
	// --------------------------------------------------
	// Zersia Custom Message Settings
	// --------------------------------------------------
	public static String CUSTOM_MSG_1001;
	public static String CUSTOM_MSG_1002;
	public static String CUSTOM_MSG_1003;
	public static String CUSTOM_MSG_1004;
	public static String CUSTOM_MSG_1005;
	public static String CUSTOM_MSG_1006;
	public static String CUSTOM_MSG_1007;
	public static String CUSTOM_MSG_1008;
	public static String CUSTOM_MSG_1009;
	public static String CUSTOM_MSG_1010;
	public static String CUSTOM_MSG_1011;
	public static String CUSTOM_MSG_1012;
	public static String CUSTOM_MSG_1013;
	public static String CUSTOM_MSG_1014;
	public static String CUSTOM_MSG_1015;
	public static String CUSTOM_MSG_1016;
	public static String CUSTOM_MSG_1017;
	public static String CUSTOM_MSG_1018;
	public static String CUSTOM_MSG_1019;
	public static String CUSTOM_MSG_1020;
	public static String CUSTOM_MSG_1021;
	public static String CUSTOM_MSG_1022;
	public static String CUSTOM_MSG_1023;
	public static String CUSTOM_MSG_1024;
	public static String CUSTOM_MSG_1025;
	public static String CUSTOM_MSG_1026;
	public static String CUSTOM_MSG_1027;
	public static String CUSTOM_MSG_1028;
	public static String CUSTOM_MSG_1029;
	public static String CUSTOM_MSG_1030;
	public static String CUSTOM_MSG_1031;
	public static String CUSTOM_MSG_1032;
	public static String CUSTOM_MSG_1033;
	public static String CUSTOM_MSG_1034;
	public static String CUSTOM_MSG_1035;
	public static String CUSTOM_MSG_1036;
	public static String CUSTOM_MSG_1037;
	public static String CUSTOM_MSG_1038;
	public static String CUSTOM_MSG_1039;
	public static String CUSTOM_MSG_1040;
	public static String CUSTOM_MSG_1041;
	public static String CUSTOM_MSG_1042;
	public static String CUSTOM_MSG_1043;
	public static String CUSTOM_MSG_1044;
	public static String CUSTOM_MSG_1045;
	public static String CUSTOM_MSG_1046;
	public static String CUSTOM_MSG_1047;
	public static String CUSTOM_MSG_1048;
	public static String CUSTOM_MSG_1049;
	public static String CUSTOM_MSG_1050;
	public static String CUSTOM_MSG_1051;
	public static String CUSTOM_MSG_1052;
	public static String CUSTOM_MSG_1053;
	public static String CUSTOM_MSG_1054;
	public static String CUSTOM_MSG_1055;
	public static String CUSTOM_MSG_1056;
	public static String CUSTOM_MSG_1057;
	public static String CUSTOM_MSG_1058;
	public static String CUSTOM_MSG_1059;
	public static String CUSTOM_MSG_1060;
	public static String CUSTOM_MSG_1061;
	public static String CUSTOM_MSG_1062;
	public static String CUSTOM_MSG_1063;
	public static String CUSTOM_MSG_1064;
	public static String CUSTOM_MSG_1065;
	public static String CUSTOM_MSG_1066;
	public static String CUSTOM_MSG_1067;
	public static String CUSTOM_MSG_1068;
	public static String CUSTOM_MSG_1069;
	public static String CUSTOM_MSG_1070;
	public static String CUSTOM_MSG_1071;
	public static String CUSTOM_MSG_1072;
	public static String CUSTOM_MSG_1073;
	public static String CUSTOM_MSG_1074;
	public static String CUSTOM_MSG_1075;
	public static String CUSTOM_MSG_1076;
	public static String CUSTOM_MSG_1077;
	public static String CUSTOM_MSG_1078;
	public static String CUSTOM_MSG_1079;
	public static String CUSTOM_MSG_1080;
	public static String CUSTOM_MSG_1081;
	public static String CUSTOM_MSG_1082;
	public static String CUSTOM_MSG_1083;
	public static String CUSTOM_MSG_1084;
	public static String CUSTOM_MSG_1085;
	public static String CUSTOM_MSG_1086;
	public static String CUSTOM_MSG_1087;
	public static String CUSTOM_MSG_1088;
	public static String CUSTOM_MSG_1089;
	public static String CUSTOM_MSG_1090;
	public static String CUSTOM_MSG_1091;
	public static String CUSTOM_MSG_1092;
	public static String CUSTOM_MSG_1093;
	public static String CUSTOM_MSG_1094;
	public static String CUSTOM_MSG_1095;
	public static String CUSTOM_MSG_1096;
	public static String CUSTOM_MSG_1097;
	public static String CUSTOM_MSG_1098;
	public static String CUSTOM_MSG_1099;
	public static String CUSTOM_MSG_1100;
	public static String CUSTOM_MSG_1101;
	public static String CUSTOM_MSG_1102;
	public static String CUSTOM_MSG_1103;
	public static String CUSTOM_MSG_1104;
	public static String CUSTOM_MSG_1105;
	public static String CUSTOM_MSG_1106;
	public static String CUSTOM_MSG_1107;
	public static String CUSTOM_MSG_1108;
	public static String CUSTOM_MSG_1109;
	public static String CUSTOM_MSG_1110;
	public static String CUSTOM_MSG_1111;
	public static String CUSTOM_MSG_1112;
	public static String CUSTOM_MSG_1113;
	public static String CUSTOM_MSG_1114;
	public static String CUSTOM_MSG_1115;
	public static String CUSTOM_MSG_1116;
	public static String CUSTOM_MSG_1117;
	public static String CUSTOM_MSG_1118;
	public static String CUSTOM_MSG_1119;
	public static String CUSTOM_MSG_1120;
	public static String CUSTOM_MSG_1121;
	public static String CUSTOM_MSG_1122;
	public static String CUSTOM_MSG_1123;
	public static String CUSTOM_MSG_1124;
	public static String CUSTOM_MSG_1125;
	public static String CUSTOM_MSG_1126;
	public static String CUSTOM_MSG_1127;
	public static String CUSTOM_MSG_1128;
	public static String CUSTOM_MSG_1129;
	public static String CUSTOM_MSG_1130;
	public static String CUSTOM_MSG_1131;
	public static String CUSTOM_MSG_1132;
	public static String CUSTOM_MSG_1133;
	public static String CUSTOM_MSG_1134;
	public static String CUSTOM_MSG_1135;
	public static String CUSTOM_MSG_1136;
	public static String CUSTOM_MSG_1137;
	public static String CUSTOM_MSG_1138;
	public static String CUSTOM_MSG_1139;
	public static String CUSTOM_MSG_1140;
	public static String CUSTOM_MSG_1141;
	public static String CUSTOM_MSG_1142;
	public static String CUSTOM_MSG_1143;
	public static String CUSTOM_MSG_1144;
	public static String CUSTOM_MSG_1145;
	public static String CUSTOM_MSG_1146;
	public static String CUSTOM_MSG_1147;
	public static String CUSTOM_MSG_1148;
	public static String CUSTOM_MSG_1149;
	public static String CUSTOM_MSG_1150;
	public static String CUSTOM_MSG_1151;
	public static String CUSTOM_MSG_1152;
	public static String CUSTOM_MSG_1153;
	public static String CUSTOM_MSG_1154;
	public static String CUSTOM_MSG_1155;
	public static String CUSTOM_MSG_1156;
	public static String CUSTOM_MSG_1157;
	public static String CUSTOM_MSG_1158;
	public static String CUSTOM_MSG_1159;
	public static String CUSTOM_MSG_1160;
	public static String CUSTOM_MSG_1161;
	public static String CUSTOM_MSG_1162;
	public static String CUSTOM_MSG_1163;
	public static String CUSTOM_MSG_1164;
	public static String CUSTOM_MSG_1165;
	public static String CUSTOM_MSG_1166;
	public static String CUSTOM_MSG_1167;
	public static String CUSTOM_MSG_1168;
	public static String CUSTOM_MSG_1169;
	public static String CUSTOM_MSG_1170;
	public static String CUSTOM_MSG_1171;
	public static String CUSTOM_MSG_1172;
	public static String CUSTOM_MSG_1173;
	public static String CUSTOM_MSG_1174;
	public static String CUSTOM_MSG_1175;
	public static String CUSTOM_MSG_1176;
	public static String CUSTOM_MSG_1177;
	public static String CUSTOM_MSG_1178;
	public static String CUSTOM_MSG_1179;
	public static String CUSTOM_MSG_1180;
	public static String CUSTOM_MSG_1181;
	public static String CUSTOM_MSG_1182;
	public static String CUSTOM_MSG_1183;
	public static String CUSTOM_MSG_1184;
	public static String CUSTOM_MSG_1185;
	public static String CUSTOM_MSG_1186;
	public static String CUSTOM_MSG_1187;
	public static String CUSTOM_MSG_1188;
	public static String CUSTOM_MSG_1189;
	public static String CUSTOM_MSG_1190;
	public static String CUSTOM_MSG_1191;
	public static String CUSTOM_MSG_1192;
	public static String CUSTOM_MSG_1193;
	public static String CUSTOM_MSG_1194;
	public static String CUSTOM_MSG_1195;
	public static String CUSTOM_MSG_1196;
	public static String CUSTOM_MSG_1197;
	public static String CUSTOM_MSG_1198;
	public static String CUSTOM_MSG_1199;
	public static String CUSTOM_MSG_1200;
	public static String CUSTOM_MSG_1201;
	public static String CUSTOM_MSG_1202;
	public static String CUSTOM_MSG_1203;
	public static String CUSTOM_MSG_1204;
	public static String CUSTOM_MSG_1205;
	public static String CUSTOM_MSG_1206;
	public static String CUSTOM_MSG_1207;
	public static String CUSTOM_MSG_1208;
	public static String CUSTOM_MSG_1209;
	public static String CUSTOM_MSG_1210;
	public static String CUSTOM_MSG_1211;
	public static String CUSTOM_MSG_1212;
	public static String CUSTOM_MSG_1213;
	public static String CUSTOM_MSG_1214;
	public static String CUSTOM_MSG_1215;
	public static String CUSTOM_MSG_1216;
	public static String CUSTOM_MSG_1217;
	public static String CUSTOM_MSG_1218;
	public static String CUSTOM_MSG_1219;
	public static String CUSTOM_MSG_1220;
	public static String CUSTOM_MSG_1221;
	public static String CUSTOM_MSG_1222;
	public static String CUSTOM_MSG_1223;
	public static String CUSTOM_MSG_1224;
	public static String CUSTOM_MSG_1225;
	public static String CUSTOM_MSG_1226;
	public static String CUSTOM_MSG_1227;
	public static String CUSTOM_MSG_1228;
	public static String CUSTOM_MSG_1229;
	public static String CUSTOM_MSG_1230;
	public static String CUSTOM_MSG_1231;
	public static String CUSTOM_MSG_1232;
	public static String CUSTOM_MSG_1233;
	public static String CUSTOM_MSG_1234;
	public static String CUSTOM_MSG_1235;
	public static String CUSTOM_MSG_1236;
	public static String CUSTOM_MSG_1237;
	public static String CUSTOM_MSG_1238;
	public static String CUSTOM_MSG_1239;
	public static String CUSTOM_MSG_1240;
	public static String CUSTOM_MSG_1241;
	public static String CUSTOM_MSG_1242;
	public static String CUSTOM_MSG_1243;
	public static String CUSTOM_MSG_1244;
	public static String CUSTOM_MSG_1245;
	public static String CUSTOM_MSG_1246;
	public static String CUSTOM_MSG_1247;
	public static String CUSTOM_MSG_1248;
	public static String CUSTOM_MSG_1249;
	public static String CUSTOM_MSG_1250;
	public static String CUSTOM_MSG_1251;
	public static String CUSTOM_MSG_1252;
	public static String CUSTOM_MSG_1253;
	public static String CUSTOM_MSG_1254;
	public static String CUSTOM_MSG_1255;
	public static String CUSTOM_MSG_1256;
	public static String CUSTOM_MSG_1257;
	public static String CUSTOM_MSG_1258;
	public static String CUSTOM_MSG_1259;
	public static String CUSTOM_MSG_1260;
	public static String CUSTOM_MSG_1261;
	public static String CUSTOM_MSG_1262;
	public static String CUSTOM_MSG_1263;
	public static String CUSTOM_MSG_1264;
	public static String CUSTOM_MSG_1265;
	public static String CUSTOM_MSG_1266;
	public static String CUSTOM_MSG_1267;
	public static String CUSTOM_MSG_1268;
	public static String CUSTOM_MSG_1269;
	public static String CUSTOM_MSG_1270;
	public static String CUSTOM_MSG_1271;
	public static String CUSTOM_MSG_1272;
	public static String CUSTOM_MSG_1273;
	public static String CUSTOM_MSG_1274;
	public static String CUSTOM_MSG_1275;
	public static String CUSTOM_MSG_1276;
	public static String CUSTOM_MSG_1277;
	public static String CUSTOM_MSG_1278;
	public static String CUSTOM_MSG_1279;
	public static String CUSTOM_MSG_1280;
	public static String CUSTOM_MSG_1281;
	public static String CUSTOM_MSG_1282;
	public static String CUSTOM_MSG_1283;
	public static String CUSTOM_MSG_1284;
	public static String CUSTOM_MSG_1285;
	public static String CUSTOM_MSG_1286;
	public static String CUSTOM_MSG_1287;
	public static String CUSTOM_MSG_1288;
	public static String CUSTOM_MSG_1289;
	public static String CUSTOM_MSG_1290;
	public static String CUSTOM_MSG_1291;
	public static String CUSTOM_MSG_1292;
	public static String CUSTOM_MSG_1293;
	public static String CUSTOM_MSG_1294;
	public static String CUSTOM_MSG_1295;
	public static String CUSTOM_MSG_1296;
	public static String CUSTOM_MSG_1297;
	public static String CUSTOM_MSG_1298;
	public static String CUSTOM_MSG_1299;
	public static String CUSTOM_MSG_1300;
	public static String CUSTOM_MSG_1301;
	public static String CUSTOM_MSG_1302;
	public static String CUSTOM_MSG_1303;
	public static String CUSTOM_MSG_1304;
	public static String CUSTOM_MSG_1305;
	public static String CUSTOM_MSG_1306;
	public static String CUSTOM_MSG_1307;
	public static String CUSTOM_MSG_1308;
	public static String CUSTOM_MSG_1309;
	public static String CUSTOM_MSG_1310;
	public static String CUSTOM_MSG_1311;
	public static String CUSTOM_MSG_1312;
	public static String CUSTOM_MSG_1313;
	public static String CUSTOM_MSG_1314;
	public static String CUSTOM_MSG_1315;
	public static String CUSTOM_MSG_1316;
	public static String CUSTOM_MSG_1317;
	public static String CUSTOM_MSG_1318;
	public static String CUSTOM_MSG_1319;
	public static String CUSTOM_MSG_1320;
	public static String CUSTOM_MSG_1321;
	public static String CUSTOM_MSG_1322;
	public static String CUSTOM_MSG_1323;
	public static String CUSTOM_MSG_1324;
	public static String CUSTOM_MSG_1325;
	public static String CUSTOM_MSG_1326;
	public static String CUSTOM_MSG_1327;
	public static String CUSTOM_MSG_1328;
	public static String CUSTOM_MSG_1329;
	public static String CUSTOM_MSG_1330;
	public static String CUSTOM_MSG_1331;
	public static String CUSTOM_MSG_1332;
	public static String CUSTOM_MSG_1333;
	public static String CUSTOM_MSG_1334;
	public static String CUSTOM_MSG_1335;
	public static String CUSTOM_MSG_1336;
	public static String CUSTOM_MSG_1337;
	public static String CUSTOM_MSG_1338;
	public static String CUSTOM_MSG_1339;
	public static String CUSTOM_MSG_1340;
	public static String CUSTOM_MSG_1341;
	public static String CUSTOM_MSG_1342;
	public static String CUSTOM_MSG_1343;
	public static String CUSTOM_MSG_1344;
	public static String CUSTOM_MSG_1345;
	public static String CUSTOM_MSG_1346;
	public static String CUSTOM_MSG_1347;
	public static String CUSTOM_MSG_1348;
	public static String CUSTOM_MSG_1349;
	public static String CUSTOM_MSG_1350;
	public static String CUSTOM_MSG_1351;
	public static String CUSTOM_MSG_1352;
	public static String CUSTOM_MSG_1353;
	public static String CUSTOM_MSG_1354;
	public static String CUSTOM_MSG_1355;
	public static String CUSTOM_MSG_1356;
	public static String CUSTOM_MSG_1357;
	public static String CUSTOM_MSG_1358;
	public static String CUSTOM_MSG_1359;
	public static String CUSTOM_MSG_1360;
	public static String CUSTOM_MSG_1361;
	public static String CUSTOM_MSG_1362;
	public static String CUSTOM_MSG_1363;
	public static String CUSTOM_MSG_1364;
	public static String CUSTOM_MSG_1365;
	public static String CUSTOM_MSG_1366;
	public static String CUSTOM_MSG_1367;
	public static String CUSTOM_MSG_1368;
	public static String CUSTOM_MSG_1369;
	public static String CUSTOM_MSG_1370;
	public static String CUSTOM_MSG_1371;
	public static String CUSTOM_MSG_1372;
	public static String CUSTOM_MSG_1373;
	public static String CUSTOM_MSG_1374;
	public static String CUSTOM_MSG_1375;
	public static String CUSTOM_MSG_1376;
	public static String CUSTOM_MSG_1377;
	public static String CUSTOM_MSG_1378;
	public static String CUSTOM_MSG_1379;
	public static String CUSTOM_MSG_1380;
	public static String CUSTOM_MSG_1381;
	public static String CUSTOM_MSG_1382;
	public static String CUSTOM_MSG_1383;
	public static String CUSTOM_MSG_1384;
	public static String CUSTOM_MSG_1385;
	public static String CUSTOM_MSG_1386;
	public static String CUSTOM_MSG_1387;
	public static String CUSTOM_MSG_1388;
	public static String CUSTOM_MSG_1389;
	public static String CUSTOM_MSG_1390;
	public static String CUSTOM_MSG_1391;
	public static String CUSTOM_MSG_1392;
	public static String CUSTOM_MSG_1393;
	public static String CUSTOM_MSG_1394;
	public static String CUSTOM_MSG_1395;
	public static String CUSTOM_MSG_1396;
	public static String CUSTOM_MSG_1397;
	public static String CUSTOM_MSG_1398;
	public static String CUSTOM_MSG_1399;
	public static String CUSTOM_MSG_1400;
	public static String CUSTOM_MSG_1401;
	public static String CUSTOM_MSG_1402;
	public static String CUSTOM_MSG_1403;
	public static String CUSTOM_MSG_1404;
	public static String CUSTOM_MSG_1405;
	public static String CUSTOM_MSG_1406;
	public static String CUSTOM_MSG_1407;
	public static String CUSTOM_MSG_1408;
	public static String CUSTOM_MSG_1409;
	public static String CUSTOM_MSG_1410;
	public static String CUSTOM_MSG_1411;
	public static String CUSTOM_MSG_1412;
	public static String CUSTOM_MSG_1413;
	public static String CUSTOM_MSG_1414;
	public static String CUSTOM_MSG_1415;
	public static String CUSTOM_MSG_1416;
	public static String CUSTOM_MSG_1417;
	public static String CUSTOM_MSG_1418;
	public static String CUSTOM_MSG_1419;
	public static String CUSTOM_MSG_1420;
	public static String CUSTOM_MSG_1421;
	public static String CUSTOM_MSG_1422;
	public static String CUSTOM_MSG_1423;
	public static String CUSTOM_MSG_1424;
	public static String CUSTOM_MSG_1425;
	public static String CUSTOM_MSG_1426;
	public static String CUSTOM_MSG_1427;
	public static String CUSTOM_MSG_1428;
	public static String CUSTOM_MSG_1429;
	public static String CUSTOM_MSG_1430;
	public static String CUSTOM_MSG_1431;
	public static String CUSTOM_MSG_1432;
	public static String CUSTOM_MSG_1433;
	public static String CUSTOM_MSG_1434;
	public static String CUSTOM_MSG_1435;
	public static String CUSTOM_MSG_1436;
	public static String CUSTOM_MSG_1437;
	public static String CUSTOM_MSG_1438;
	public static String CUSTOM_MSG_1439;
	public static String CUSTOM_MSG_1440;
	public static String CUSTOM_MSG_1441;
	public static String CUSTOM_MSG_1442;
	public static String CUSTOM_MSG_1443;
	public static String CUSTOM_MSG_1444;
	public static String CUSTOM_MSG_1445;
	public static String CUSTOM_MSG_1446;
	public static String CUSTOM_MSG_1447;
	public static String CUSTOM_MSG_1448;
	public static String CUSTOM_MSG_1449;
	public static String CUSTOM_MSG_1450;
	public static String CUSTOM_MSG_1451;
	public static String CUSTOM_MSG_1452;
	public static String CUSTOM_MSG_1453;
	public static String CUSTOM_MSG_1454;
	public static String CUSTOM_MSG_1455;
	public static String CUSTOM_MSG_1456;
	public static String CUSTOM_MSG_1457;
	public static String CUSTOM_MSG_1458;
	public static String CUSTOM_MSG_1459;
	public static String CUSTOM_MSG_1460;
	public static String CUSTOM_MSG_1461;
	public static String CUSTOM_MSG_1462;
	public static String CUSTOM_MSG_1463;
	public static String CUSTOM_MSG_1464;
	public static String CUSTOM_MSG_1465;
	public static String CUSTOM_MSG_1466;
	public static String CUSTOM_MSG_1467;
	public static String CUSTOM_MSG_1468;
	public static String CUSTOM_MSG_1469;
	public static String CUSTOM_MSG_1470;
	public static String CUSTOM_MSG_1471;
	public static String CUSTOM_MSG_1472;
	public static String CUSTOM_MSG_1473;
	public static String CUSTOM_MSG_1474;
	public static String CUSTOM_MSG_1475;
	public static String CUSTOM_MSG_1476;
	public static String CUSTOM_MSG_1477;
	public static String CUSTOM_MSG_1478;
	public static String CUSTOM_MSG_1479;
	public static String CUSTOM_MSG_1480;
	public static String CUSTOM_MSG_1481;
	public static String CUSTOM_MSG_1482;
	public static String CUSTOM_MSG_1483;
	public static String CUSTOM_MSG_1484;
	public static String CUSTOM_MSG_1485;
	public static String CUSTOM_MSG_1486;
	public static String CUSTOM_MSG_1487;
	public static String CUSTOM_MSG_1488;
	public static String CUSTOM_MSG_1489;
	public static String CUSTOM_MSG_1490;
	public static String CUSTOM_MSG_1491;
	public static String CUSTOM_MSG_1492;
	public static String CUSTOM_MSG_1493;
	public static String CUSTOM_MSG_1494;
	public static String CUSTOM_MSG_1495;
	public static String CUSTOM_MSG_1496;
	public static String CUSTOM_MSG_1497;
	public static String CUSTOM_MSG_1498;
	public static String CUSTOM_MSG_1499;
	public static String CUSTOM_MSG_1500;
	public static String CUSTOM_MSG_1501;
	public static String CUSTOM_MSG_1502;
	public static String CUSTOM_MSG_1503;
	public static String CUSTOM_MSG_1504;
	public static String CUSTOM_MSG_1505;
	public static String CUSTOM_MSG_1506;
	public static String CUSTOM_MSG_1507;
	public static String CUSTOM_MSG_1508;
	public static String CUSTOM_MSG_1509;
	public static String CUSTOM_MSG_1510;
	public static String CUSTOM_MSG_1511;
	public static String CUSTOM_MSG_1512;
	public static String CUSTOM_MSG_1513;
	public static String CUSTOM_MSG_1514;
	public static String CUSTOM_MSG_1515;
	public static String CUSTOM_MSG_1516;
	public static String CUSTOM_MSG_1517;
	public static String CUSTOM_MSG_1518;
	public static String CUSTOM_MSG_1519;
	public static String CUSTOM_MSG_1520;
	public static String CUSTOM_MSG_1521;
	public static String CUSTOM_MSG_1522;
	public static String CUSTOM_MSG_1523;
	public static String CUSTOM_MSG_1524;
	public static String CUSTOM_MSG_1525;
	public static String CUSTOM_MSG_1526;
	public static String CUSTOM_MSG_1527;
	public static String CUSTOM_MSG_1528;
	public static String CUSTOM_MSG_1529;
	public static String CUSTOM_MSG_1530;
	public static String CUSTOM_MSG_1531;
	public static String CUSTOM_MSG_1532;
	public static String CUSTOM_MSG_1533;
	public static String CUSTOM_MSG_1534;
	public static String CUSTOM_MSG_1535;
	public static String CUSTOM_MSG_1536;
	public static String CUSTOM_MSG_1537;
	public static String CUSTOM_MSG_1538;
	public static String CUSTOM_MSG_1539;
	public static String CUSTOM_MSG_1540;
	public static String CUSTOM_MSG_1541;
	public static String CUSTOM_MSG_1542;
	public static String CUSTOM_MSG_1543;
	public static String CUSTOM_MSG_1544;
	public static String CUSTOM_MSG_1545;
	public static String CUSTOM_MSG_1546;
	public static String CUSTOM_MSG_1547;
	public static String CUSTOM_MSG_1548;
	public static String CUSTOM_MSG_1549;
	public static String CUSTOM_MSG_1550;
	public static String CUSTOM_MSG_1551;
	public static String CUSTOM_MSG_1552;
	public static String CUSTOM_MSG_1553;
	public static String CUSTOM_MSG_1554;
	public static String CUSTOM_MSG_1555;
	public static String CUSTOM_MSG_1556;
	public static String CUSTOM_MSG_1557;
	public static String CUSTOM_MSG_1558;
	public static String CUSTOM_MSG_1559;
	public static String CUSTOM_MSG_1560;
	public static String CUSTOM_MSG_1561;
	public static String CUSTOM_MSG_1562;
	public static String CUSTOM_MSG_1563;
	public static String CUSTOM_MSG_1564;
	public static String CUSTOM_MSG_1565;
	public static String CUSTOM_MSG_1566;
	public static String CUSTOM_MSG_1567;
	public static String CUSTOM_MSG_1568;
	public static String CUSTOM_MSG_1569;
	public static String CUSTOM_MSG_1570;
	public static String CUSTOM_MSG_1571;
	public static String CUSTOM_MSG_1572;
	public static String CUSTOM_MSG_1573;
	public static String CUSTOM_MSG_1574;
	public static String CUSTOM_MSG_1575;
	public static String CUSTOM_MSG_1576;
	public static String CUSTOM_MSG_1577;
	public static String CUSTOM_MSG_1578;
	public static String CUSTOM_MSG_1579;
	public static String CUSTOM_MSG_1580;
	public static String CUSTOM_MSG_1581;
	public static String CUSTOM_MSG_1582;
	public static String CUSTOM_MSG_1583;
	public static String CUSTOM_MSG_1584;
	public static String CUSTOM_MSG_1585;
	public static String CUSTOM_MSG_1586;
	public static String CUSTOM_MSG_1587;
	public static String CUSTOM_MSG_1588;
	public static String CUSTOM_MSG_1589;
	public static String CUSTOM_MSG_1590;
	public static String CUSTOM_MSG_1591;
	public static String CUSTOM_MSG_1592;
	public static String CUSTOM_MSG_1593;
	public static String CUSTOM_MSG_1594;
	public static String CUSTOM_MSG_1595;
	public static String CUSTOM_MSG_1596;
	public static String CUSTOM_MSG_1597;
	public static String CUSTOM_MSG_1598;
	public static String CUSTOM_MSG_1599;
	public static String CUSTOM_MSG_1600;
	public static String CUSTOM_MSG_1601;
	public static String CUSTOM_MSG_1602;
	public static String CUSTOM_MSG_1603;
	public static String CUSTOM_MSG_1604;
	public static String CUSTOM_MSG_1605;
	public static String CUSTOM_MSG_1606;
	public static String CUSTOM_MSG_1607;
	public static String CUSTOM_MSG_1608;
	public static String CUSTOM_MSG_1609;
	public static String CUSTOM_MSG_1610;
	public static String CUSTOM_MSG_1611;
	public static String CUSTOM_MSG_1612;
	public static String CUSTOM_MSG_1613;
	public static String CUSTOM_MSG_1614;
	public static String CUSTOM_MSG_1615;
	public static String CUSTOM_MSG_1616;
	public static String CUSTOM_MSG_1617;
	public static String CUSTOM_MSG_1618;
	public static String CUSTOM_MSG_1619;
	public static String CUSTOM_MSG_1620;
	public static String CUSTOM_MSG_1621;
	public static String CUSTOM_MSG_1622;
	public static String CUSTOM_MSG_1623;
	public static String CUSTOM_MSG_1624;
	public static String CUSTOM_MSG_1625;
	public static String CUSTOM_MSG_1626;
	public static String CUSTOM_MSG_1627;
	public static String CUSTOM_MSG_1628;
	public static String CUSTOM_MSG_1629;
	public static String CUSTOM_MSG_1630;
	public static String CUSTOM_MSG_1631;
	public static String CUSTOM_MSG_1632;
	public static String CUSTOM_MSG_1633;
	public static String CUSTOM_MSG_1634;
	public static String CUSTOM_MSG_1635;
	public static String CUSTOM_MSG_1636;
	public static String CUSTOM_MSG_1637;
	public static String CUSTOM_MSG_1638;
	public static String CUSTOM_MSG_1639;
	public static String CUSTOM_MSG_1640;
	public static String CUSTOM_MSG_1641;
	public static String CUSTOM_MSG_1642;
	public static String CUSTOM_MSG_1643;
	public static String CUSTOM_MSG_1644;
	public static String CUSTOM_MSG_1645;
	public static String CUSTOM_MSG_1646;
	public static String CUSTOM_MSG_1647;
	public static String CUSTOM_MSG_1648;
	public static String CUSTOM_MSG_1649;
	public static String CUSTOM_MSG_1650;
	public static String CUSTOM_MSG_1651;
	public static String CUSTOM_MSG_1652;
	public static String CUSTOM_MSG_1653;
	public static String CUSTOM_MSG_1654;
	public static String CUSTOM_MSG_1655;
	public static String CUSTOM_MSG_1656;
	public static String CUSTOM_MSG_1657;
	public static String CUSTOM_MSG_1658;
	public static String CUSTOM_MSG_1659;
	public static String CUSTOM_MSG_1660;
	public static String CUSTOM_MSG_1661;
	public static String CUSTOM_MSG_1662;
	public static String CUSTOM_MSG_1663;
	public static String CUSTOM_MSG_1664;
	public static String CUSTOM_MSG_1665;
	public static String CUSTOM_MSG_1666;
	public static String CUSTOM_MSG_1667;
	public static String CUSTOM_MSG_1668;
	public static String CUSTOM_MSG_1669;
	public static String CUSTOM_MSG_1670;
	public static String CUSTOM_MSG_1671;
	public static String CUSTOM_MSG_1672;
	public static String CUSTOM_MSG_1673;
	public static String CUSTOM_MSG_1674;
	public static String CUSTOM_MSG_1675;
	public static String CUSTOM_MSG_1676;
	public static String CUSTOM_MSG_1677;
	public static String CUSTOM_MSG_1678;
	public static String CUSTOM_MSG_1679;
	public static String CUSTOM_MSG_1680;
	public static String CUSTOM_MSG_1681;
	public static String CUSTOM_MSG_1682;
	public static String CUSTOM_MSG_1683;
	public static String CUSTOM_MSG_1684;
	public static String CUSTOM_MSG_1685;
	public static String CUSTOM_MSG_1686;
	public static String CUSTOM_MSG_1687;
	public static String CUSTOM_MSG_1688;
	public static String CUSTOM_MSG_1689;
	public static String CUSTOM_MSG_1690;
	public static String CUSTOM_MSG_1691;
	public static String CUSTOM_MSG_1692;
	public static String CUSTOM_MSG_1693;
	public static String CUSTOM_MSG_1694;
	public static String CUSTOM_MSG_1695;
	public static String CUSTOM_MSG_1696;
	public static String CUSTOM_MSG_1697;
	public static String CUSTOM_MSG_1698;
	public static String CUSTOM_MSG_1699;
	public static String CUSTOM_MSG_1700;
	public static String CUSTOM_MSG_1701;
	public static String CUSTOM_MSG_1702;
	public static String CUSTOM_MSG_1703;
	public static String CUSTOM_MSG_1704;
	public static String CUSTOM_MSG_1705;
	public static String CUSTOM_MSG_1706;
	public static String CUSTOM_MSG_1707;
	public static String CUSTOM_MSG_1708;
	public static String CUSTOM_MSG_1709;
	public static String CUSTOM_MSG_1710;
	public static String CUSTOM_MSG_1711;
	public static String CUSTOM_MSG_1712;
	public static String CUSTOM_MSG_1713;
	public static String CUSTOM_MSG_1714;
	public static String CUSTOM_MSG_1715;
	public static String CUSTOM_MSG_1716;
	public static String CUSTOM_MSG_1717;
	public static String CUSTOM_MSG_1718;
	public static String CUSTOM_MSG_1719;
	public static String CUSTOM_MSG_1720;
	public static String CUSTOM_MSG_1721;
	public static String CUSTOM_MSG_1722;
	public static String CUSTOM_MSG_1723;
	public static String CUSTOM_MSG_1724;
	public static String CUSTOM_MSG_1725;
	public static String CUSTOM_MSG_1726;
	public static String CUSTOM_MSG_1727;
	public static String CUSTOM_MSG_1728;
	public static String CUSTOM_MSG_1729;
	public static String CUSTOM_MSG_1730;
	public static String CUSTOM_MSG_1731;
	public static String CUSTOM_MSG_1732;
	public static String CUSTOM_MSG_1733;
	public static String CUSTOM_MSG_1734;
	public static String CUSTOM_MSG_1735;
	public static String CUSTOM_MSG_1736;
	public static String CUSTOM_MSG_1737;
	public static String CUSTOM_MSG_1738;
	public static String CUSTOM_MSG_1739;
	public static String CUSTOM_MSG_1740;
	public static String CUSTOM_MSG_1741;
	public static String CUSTOM_MSG_1742;
	public static String CUSTOM_MSG_1743;
	public static String CUSTOM_MSG_1744;
	public static String CUSTOM_MSG_1745;
	public static String CUSTOM_MSG_1746;
	public static String CUSTOM_MSG_1747;
	public static String CUSTOM_MSG_1748;
	public static String CUSTOM_MSG_1749;
	public static String CUSTOM_MSG_1750;
	public static String CUSTOM_MSG_1751;
	public static String CUSTOM_MSG_1752;
	public static String CUSTOM_MSG_1753;
	public static String CUSTOM_MSG_1754;
	public static String CUSTOM_MSG_1755;
	public static String CUSTOM_MSG_1756;
	public static String CUSTOM_MSG_1757;
	public static String CUSTOM_MSG_1758;
	public static String CUSTOM_MSG_1759;
	public static String CUSTOM_MSG_1760;
	public static String CUSTOM_MSG_1761;
	public static String CUSTOM_MSG_1762;
	public static String CUSTOM_MSG_1763;
	public static String CUSTOM_MSG_1764;
	public static String CUSTOM_MSG_1765;
	public static String CUSTOM_MSG_1766;
	public static String CUSTOM_MSG_1767;
	public static String CUSTOM_MSG_1768;
	public static String CUSTOM_MSG_1769;
	public static String CUSTOM_MSG_1770;
	public static String CUSTOM_MSG_1771;
	public static String CUSTOM_MSG_1772;
	public static String CUSTOM_MSG_1773;
	public static String CUSTOM_MSG_1774;
	public static String CUSTOM_MSG_1775;
	public static String CUSTOM_MSG_1776;
	public static String CUSTOM_MSG_1777;
	public static String CUSTOM_MSG_1778;
	public static String CUSTOM_MSG_1779;
	public static String CUSTOM_MSG_1780;
	public static String CUSTOM_MSG_1781;
	public static String CUSTOM_MSG_1782;
	public static String CUSTOM_MSG_1783;
	public static String CUSTOM_MSG_1784;
	public static String CUSTOM_MSG_1785;
	public static String CUSTOM_MSG_1786;
	public static String CUSTOM_MSG_1787;
	public static String CUSTOM_MSG_1788;
	public static String CUSTOM_MSG_1789;
	public static String CUSTOM_MSG_1790;
	public static String CUSTOM_MSG_1791;
	public static String CUSTOM_MSG_1792;
	public static String CUSTOM_MSG_1793;
	public static String CUSTOM_MSG_1794;
	public static String CUSTOM_MSG_1795;
	public static String CUSTOM_MSG_1796;
	public static String CUSTOM_MSG_1797;
	public static String CUSTOM_MSG_1798;
	public static String CUSTOM_MSG_1799;
	public static String CUSTOM_MSG_1800;
	public static String CUSTOM_MSG_1801;
	public static String CUSTOM_MSG_1802;
	public static String CUSTOM_MSG_1803;
	public static String CUSTOM_MSG_1804;
	public static String CUSTOM_MSG_1805;
	public static String CUSTOM_MSG_1806;
	public static String CUSTOM_MSG_1807;
	public static String CUSTOM_MSG_1808;
	public static String CUSTOM_MSG_1809;
	public static String CUSTOM_MSG_1810;
	public static String CUSTOM_MSG_1811;
	public static String CUSTOM_MSG_1812;
	public static String CUSTOM_MSG_1813;
	public static String CUSTOM_MSG_1814;
	public static String CUSTOM_MSG_1815;
	public static String CUSTOM_MSG_1816;
	public static String CUSTOM_MSG_1817;
	public static String CUSTOM_MSG_1818;
	public static String CUSTOM_MSG_1819;
	public static String CUSTOM_MSG_1820;
	public static String CUSTOM_MSG_1821;
	public static String CUSTOM_MSG_1822;
	public static String CUSTOM_MSG_1823;
	public static String CUSTOM_MSG_1824;
	public static String CUSTOM_MSG_1825;
	public static String CUSTOM_MSG_1826;
	public static String CUSTOM_MSG_1827;
	public static String CUSTOM_MSG_1828;
	public static String CUSTOM_MSG_1829;
	public static String CUSTOM_MSG_1830;
	public static String CUSTOM_MSG_1831;
	public static String CUSTOM_MSG_1832;
	public static String CUSTOM_MSG_1833;
	public static String CUSTOM_MSG_1834;
	public static String CUSTOM_MSG_1835;
	public static String CUSTOM_MSG_1836;
	public static String CUSTOM_MSG_1837;
	public static String CUSTOM_MSG_1838;
	public static String CUSTOM_MSG_1839;
	public static String CUSTOM_MSG_1840;
	public static String CUSTOM_MSG_1841;
	public static String CUSTOM_MSG_1842;
	public static String CUSTOM_MSG_1843;
	public static String CUSTOM_MSG_1844;
	public static String CUSTOM_MSG_1845;
	public static String CUSTOM_MSG_1846;
	public static String CUSTOM_MSG_1847;
	public static String CUSTOM_MSG_1848;
	public static String CUSTOM_MSG_1849;
	public static String CUSTOM_MSG_1850;
	public static String CUSTOM_MSG_1851;
	public static String CUSTOM_MSG_1852;
	public static String CUSTOM_MSG_1853;
	public static String CUSTOM_MSG_1854;
	public static String CUSTOM_MSG_1855;
	public static String CUSTOM_MSG_1856;
	public static String CUSTOM_MSG_1857;
	public static String CUSTOM_MSG_1858;
	public static String CUSTOM_MSG_1859;
	public static String CUSTOM_MSG_1860;
	public static String CUSTOM_MSG_1861;
	public static String CUSTOM_MSG_1862;
	public static String CUSTOM_MSG_1863;
	public static String CUSTOM_MSG_1864;
	public static String CUSTOM_MSG_1865;
	public static String CUSTOM_MSG_1866;
	public static String CUSTOM_MSG_1867;
	public static String CUSTOM_MSG_1868;
	public static String CUSTOM_MSG_1869;
	public static String CUSTOM_MSG_1870;
	public static String CUSTOM_MSG_1871;
	public static String CUSTOM_MSG_1872;
	public static String CUSTOM_MSG_1873;
	public static String CUSTOM_MSG_1874;
	public static String CUSTOM_MSG_1875;
	public static String CUSTOM_MSG_1876;
	public static String CUSTOM_MSG_1877;
	public static String CUSTOM_MSG_1878;
	public static String CUSTOM_MSG_1879;
	public static String CUSTOM_MSG_1880;
	public static String CUSTOM_MSG_1881;
	public static String CUSTOM_MSG_1882;
	public static String CUSTOM_MSG_1883;
	public static String CUSTOM_MSG_1884;
	public static String CUSTOM_MSG_1885;
	public static String CUSTOM_MSG_1886;
	public static String CUSTOM_MSG_1887;
	public static String CUSTOM_MSG_1888;
	public static String CUSTOM_MSG_1889;
	public static String CUSTOM_MSG_1890;
	public static String CUSTOM_MSG_1891;
	public static String CUSTOM_MSG_1892;
	public static String CUSTOM_MSG_1893;
	public static String CUSTOM_MSG_1894;
	public static String CUSTOM_MSG_1895;
	public static String CUSTOM_MSG_1896;
	public static String CUSTOM_MSG_1897;
	public static String CUSTOM_MSG_1898;
	public static String CUSTOM_MSG_1899;
	public static String CUSTOM_MSG_1900;
	public static String CUSTOM_MSG_1901;
	public static String CUSTOM_MSG_1902;
	public static String CUSTOM_MSG_1903;
	public static String CUSTOM_MSG_1904;
	public static String CUSTOM_MSG_1905;
	public static String CUSTOM_MSG_1906;
	public static String CUSTOM_MSG_1907;
	public static String CUSTOM_MSG_1908;
	public static String CUSTOM_MSG_1909;
	public static String CUSTOM_MSG_1910;
	public static String CUSTOM_MSG_1911;
	public static String CUSTOM_MSG_1912;
	public static String CUSTOM_MSG_1913;
	public static String CUSTOM_MSG_1914;
	public static String CUSTOM_MSG_1915;
	public static String CUSTOM_MSG_1916;
	public static String CUSTOM_MSG_1917;
	public static String CUSTOM_MSG_1918;
	public static String CUSTOM_MSG_1919;
	public static String CUSTOM_MSG_1920;
	public static String CUSTOM_MSG_1921;
	public static String CUSTOM_MSG_1922;
	public static String CUSTOM_MSG_1923;
	public static String CUSTOM_MSG_1924;
	public static String CUSTOM_MSG_1925;
	public static String CUSTOM_MSG_1926;
	public static String CUSTOM_MSG_1927;
	public static String CUSTOM_MSG_1928;
	public static String CUSTOM_MSG_1929;
	public static String CUSTOM_MSG_1930;
	public static String CUSTOM_MSG_1931;
	public static String CUSTOM_MSG_1932;
	public static String CUSTOM_MSG_1933;
	public static String CUSTOM_MSG_1934;
	public static String CUSTOM_MSG_1935;
	public static String CUSTOM_MSG_1936;
	public static String CUSTOM_MSG_1937;
	public static String CUSTOM_MSG_1938;
	public static String CUSTOM_MSG_1939;
	public static String CUSTOM_MSG_1940;
	public static String CUSTOM_MSG_1941;
	public static String CUSTOM_MSG_1942;
	public static String CUSTOM_MSG_1943;
	public static String CUSTOM_MSG_1944;
	public static String CUSTOM_MSG_1945;
	public static String CUSTOM_MSG_1946;
	public static String CUSTOM_MSG_1947;
	public static String CUSTOM_MSG_1948;
	public static String CUSTOM_MSG_1949;
	public static String CUSTOM_MSG_1950;
	public static String CUSTOM_MSG_1951;
	public static String CUSTOM_MSG_1952;
	public static String CUSTOM_MSG_1953;
	public static String CUSTOM_MSG_1954;
	public static String CUSTOM_MSG_1955;
	public static String CUSTOM_MSG_1956;
	public static String CUSTOM_MSG_1957;
	public static String CUSTOM_MSG_1958;
	public static String CUSTOM_MSG_1959;
	public static String CUSTOM_MSG_1960;
	public static String CUSTOM_MSG_1961;
	public static String CUSTOM_MSG_1962;
	public static String CUSTOM_MSG_1963;
	public static String CUSTOM_MSG_1964;
	public static String CUSTOM_MSG_1965;
	public static String CUSTOM_MSG_1966;
	public static String CUSTOM_MSG_1967;
	public static String CUSTOM_MSG_1968;
	public static String CUSTOM_MSG_1969;
	public static String CUSTOM_MSG_1970;
	public static String CUSTOM_MSG_1971;
	public static String CUSTOM_MSG_1972;
	public static String CUSTOM_MSG_1973;
	public static String CUSTOM_MSG_1974;
	public static String CUSTOM_MSG_1975;
	public static String CUSTOM_MSG_1976;
	public static String CUSTOM_MSG_1977;
	public static String CUSTOM_MSG_1978;
	public static String CUSTOM_MSG_1979;
	public static String CUSTOM_MSG_1980;
	public static String CUSTOM_MSG_1981;
	public static String CUSTOM_MSG_1982;
	public static String CUSTOM_MSG_1983;
	public static String CUSTOM_MSG_1984;
	public static String CUSTOM_MSG_1985;
	public static String CUSTOM_MSG_1986;
	public static String CUSTOM_MSG_1987;
	public static String CUSTOM_MSG_1988;
	public static String CUSTOM_MSG_1989;
	public static String CUSTOM_MSG_1990;
	public static String CUSTOM_MSG_1991;
	public static String CUSTOM_MSG_1992;
	public static String CUSTOM_MSG_1993;
	public static String CUSTOM_MSG_1994;
	public static String CUSTOM_MSG_1995;
	public static String CUSTOM_MSG_1996;
	public static String CUSTOM_MSG_1997;
	public static String CUSTOM_MSG_1998;
	public static String CUSTOM_MSG_1999;
	public static String CUSTOM_MSG_2000;
	
	// --------------------------------------------------
	// L2J Variable Definitions
	// --------------------------------------------------
	public static boolean ALT_GAME_DELEVEL;
	public static boolean DECREASE_SKILL_LEVEL;
	public static double ALT_WEIGHT_LIMIT;
	public static int RUN_SPD_BOOST;
	public static int DEATH_PENALTY_CHANCE;
	public static double RESPAWN_RESTORE_CP;
	public static double RESPAWN_RESTORE_HP;
	public static double RESPAWN_RESTORE_MP;
	public static boolean ALT_GAME_TIREDNESS;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	public static boolean ENABLE_MODIFY_SKILL_REUSE;
	public static Map<Integer, Integer> SKILL_REUSE_LIST;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_FS_SKILLS;
	public static boolean AUTO_LOOT_HERBS;
	public static byte BUFFS_MAX_AMOUNT;
	public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
	public static byte DANCES_MAX_AMOUNT;
	public static boolean DANCE_CANCEL_BUFF;
	public static boolean DANCE_CONSUME_ADDITIONAL_MP;
	public static boolean ALT_STORE_DANCES;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static boolean STORE_SKILL_COOLTIME;
	public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
	public static boolean SUMMON_STORE_SKILL_COOLTIME;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static long EFFECT_TICK_RATIO;
	public static boolean ALLOW_CLASS_MASTERS;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ALTERNATE_CLASS_MASTER;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
	public static int FEE_DELETE_TRANSFER_SKILLS;
	public static int FEE_DELETE_SUBCLASS_SKILLS;
	public static boolean RESTORE_SERVITOR_ON_RECONNECT;
	public static boolean RESTORE_PET_ON_RECONNECT;
	public static double MAX_BONUS_EXP;
	public static double MAX_BONUS_SP;
	public static int MAX_RUN_SPEED;
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int MAX_EVASION;
	public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
	public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
	public static byte MAX_SUBCLASS;
	public static byte BASE_SUBCLASS_LEVEL;
	public static byte MAX_SUBCLASS_LEVEL;
	public static int MAX_PVTSTORESELL_SLOTS_DWARF;
	public static int MAX_PVTSTORESELL_SLOTS_OTHER;
	public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
	public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int ALT_FREIGHT_SLOTS;
	public static int ALT_FREIGHT_PRICE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	public static int MAX_PERSONAL_FAME_POINTS;
	public static int FORTRESS_ZONE_FAME_TASK_FREQUENCY;
	public static int FORTRESS_ZONE_FAME_AQUIRE_POINTS;
	public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
	public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
	public static boolean FAME_FOR_DEAD_PLAYERS;
	public static boolean IS_CRAFTING_ENABLED;
	public static boolean CRAFT_MASTERWORK;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_GAME_CREATION;
	public static double ALT_GAME_CREATION_SPEED;
	public static double ALT_GAME_CREATION_XP_RATE;
	public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
	public static double ALT_GAME_CREATION_SP_RATE;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	public static int ALT_CLAN_LEADER_DATE_CHANGE;
	public static String ALT_CLAN_LEADER_HOUR_CHANGE;
	public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static boolean REMOVE_CASTLE_CIRCLETS;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_LEAVE_PARTY_LEADER;
	public static boolean INITIAL_EQUIPMENT_EVENT;
	public static long STARTING_ADENA;
	public static byte STARTING_LEVEL;
	public static int STARTING_SP;
	public static long MAX_ADENA;
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_RAIDS;
	public static int LOOT_RAIDS_PRIVILEGE_INTERVAL;
	public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
	public static int UNSTUCK_INTERVAL;
	public static int TELEPORT_WATCHDOG_TIMEOUT;
	public static int PLAYER_SPAWN_PROTECTION;
	public static List<Integer> SPAWN_PROTECTION_ALLOWED_ITEMS;
	public static int PLAYER_TELEPORT_PROTECTION;
	public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
	public static boolean OFFSET_ON_TELEPORT_ENABLED;
	public static int MAX_OFFSET_ON_TELEPORT;
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	public static boolean ALT_GAME_FREE_TELEPORT;
	public static int DELETE_DAYS;
	public static float ALT_GAME_EXPONENT_XP;
	public static float ALT_GAME_EXPONENT_SP;
	public static String PARTY_XP_CUTOFF_METHOD;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static int[][] PARTY_XP_CUTOFF_GAPS;
	public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
	public static boolean DISABLE_TUTORIAL;
	public static boolean EXPERTISE_PENALTY;
	public static boolean STORE_RECIPE_SHOPLIST;
	public static boolean STORE_UI_SETTINGS;
	public static String[] FORBIDDEN_NAMES;
	public static boolean SILENCE_MODE_EXCLUDE;
	public static boolean ALT_VALIDATE_TRIGGER_SKILLS;
	
	// --------------------------------------------------
	// ClanHall Settings
	// --------------------------------------------------
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	public static boolean CH_BUFF_FREE;
	// --------------------------------------------------
	// Castle Settings
	// --------------------------------------------------
	public static long CS_TELE_FEE_RATIO;
	public static int CS_TELE1_FEE;
	public static int CS_TELE2_FEE;
	public static long CS_MPREG_FEE_RATIO;
	public static int CS_MPREG1_FEE;
	public static int CS_MPREG2_FEE;
	public static long CS_HPREG_FEE_RATIO;
	public static int CS_HPREG1_FEE;
	public static int CS_HPREG2_FEE;
	public static long CS_EXPREG_FEE_RATIO;
	public static int CS_EXPREG1_FEE;
	public static int CS_EXPREG2_FEE;
	public static long CS_SUPPORT_FEE_RATIO;
	public static int CS_SUPPORT1_FEE;
	public static int CS_SUPPORT2_FEE;
	public static List<Integer> SIEGE_HOUR_LIST;
	public static int OUTER_DOOR_UPGRADE_PRICE2;
	public static int OUTER_DOOR_UPGRADE_PRICE3;
	public static int OUTER_DOOR_UPGRADE_PRICE5;
	public static int INNER_DOOR_UPGRADE_PRICE2;
	public static int INNER_DOOR_UPGRADE_PRICE3;
	public static int INNER_DOOR_UPGRADE_PRICE5;
	public static int WALL_UPGRADE_PRICE2;
	public static int WALL_UPGRADE_PRICE3;
	public static int WALL_UPGRADE_PRICE5;
	public static int TRAP_UPGRADE_PRICE1;
	public static int TRAP_UPGRADE_PRICE2;
	public static int TRAP_UPGRADE_PRICE3;
	public static int TRAP_UPGRADE_PRICE4;
	
	// --------------------------------------------------
	// Fortress Settings
	// --------------------------------------------------
	public static long FS_TELE_FEE_RATIO;
	public static int FS_TELE1_FEE;
	public static int FS_TELE2_FEE;
	public static long FS_MPREG_FEE_RATIO;
	public static int FS_MPREG1_FEE;
	public static int FS_MPREG2_FEE;
	public static long FS_HPREG_FEE_RATIO;
	public static int FS_HPREG1_FEE;
	public static int FS_HPREG2_FEE;
	public static long FS_EXPREG_FEE_RATIO;
	public static int FS_EXPREG1_FEE;
	public static int FS_EXPREG2_FEE;
	public static long FS_SUPPORT_FEE_RATIO;
	public static int FS_SUPPORT1_FEE;
	public static int FS_SUPPORT2_FEE;
	public static int FS_BLOOD_OATH_COUNT;
	public static int FS_UPDATE_FRQ;
	public static int FS_MAX_SUPPLY_LEVEL;
	public static int FS_FEE_FOR_CASTLE;
	public static int FS_MAX_OWN_TIME;
	// --------------------------------------------------
	// Feature Settings
	// --------------------------------------------------
	public static int TAKE_FORT_POINTS;
	public static int LOOSE_FORT_POINTS;
	public static int TAKE_CASTLE_POINTS;
	public static int LOOSE_CASTLE_POINTS;
	public static int CASTLE_DEFENDED_POINTS;
	public static int FESTIVAL_WIN_POINTS;
	public static int HERO_POINTS;
	public static int ROYAL_GUARD_COST;
	public static int KNIGHT_UNIT_COST;
	public static int KNIGHT_REINFORCE_COST;
	public static int BALLISTA_POINTS;
	public static int BLOODALLIANCE_POINTS;
	public static int BLOODOATH_POINTS;
	public static int KNIGHTSEPAULETTE_POINTS;
	public static int REPUTATION_SCORE_PER_KILL;
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
	public static int RAID_RANKING_1ST;
	public static int RAID_RANKING_2ND;
	public static int RAID_RANKING_3RD;
	public static int RAID_RANKING_4TH;
	public static int RAID_RANKING_5TH;
	public static int RAID_RANKING_6TH;
	public static int RAID_RANKING_7TH;
	public static int RAID_RANKING_8TH;
	public static int RAID_RANKING_9TH;
	public static int RAID_RANKING_10TH;
	public static int RAID_RANKING_UP_TO_50TH;
	public static int RAID_RANKING_UP_TO_100TH;
	public static int CLAN_LEVEL_6_COST;
	public static int CLAN_LEVEL_7_COST;
	public static int CLAN_LEVEL_8_COST;
	public static int CLAN_LEVEL_9_COST;
	public static int CLAN_LEVEL_10_COST;
	public static int CLAN_LEVEL_11_COST;
	public static int CLAN_LEVEL_6_REQUIREMENT;
	public static int CLAN_LEVEL_7_REQUIREMENT;
	public static int CLAN_LEVEL_8_REQUIREMENT;
	public static int CLAN_LEVEL_9_REQUIREMENT;
	public static int CLAN_LEVEL_10_REQUIREMENT;
	public static int CLAN_LEVEL_11_REQUIREMENT;
	public static boolean ALLOW_WYVERN_ALWAYS;
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
	
	// --------------------------------------------------
	// General Settings
	// --------------------------------------------------
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static boolean SERVER_LIST_BRACKET;
	public static int SERVER_LIST_TYPE;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_GMONLY;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	public static boolean GM_STARTUP_DIET_MODE;
	public static boolean GM_ITEM_RESTRICTION;
	public static boolean GM_SKILL_RESTRICTION;
	public static boolean GM_TRADE_RESTRICTED_ITEMS;
	public static boolean GM_RESTART_FIGHTING;
	public static boolean GM_ANNOUNCER_NAME;
	public static boolean GM_CRITANNOUNCER_NAME;
	public static boolean GM_GIVE_SPECIAL_SKILLS;
	public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
	public static boolean GAMEGUARD_ENFORCE;
	public static boolean GAMEGUARD_PROHIBITACTION;
	public static boolean LOG_CHAT;
	public static boolean LOG_AUTO_ANNOUNCEMENTS;
	public static boolean LOG_ITEMS;
	public static boolean LOG_ITEMS_SMALL_LOG;
	public static boolean LOG_ITEM_ENCHANTS;
	public static boolean LOG_SKILL_ENCHANTS;
	public static boolean GMAUDIT;
	public static boolean SKILL_CHECK_ENABLE;
	public static boolean SKILL_CHECK_REMOVE;
	public static boolean SKILL_CHECK_GM;
	public static boolean DEBUG;
	public static boolean DEBUG_INSTANCES;
	public static boolean HTML_ACTION_CACHE_DEBUG;
	public static boolean PACKET_HANDLER_DEBUG;
	public static boolean DEVELOPER;
	public static boolean ALT_DEV_NO_HANDLERS;
	public static boolean ALT_DEV_NO_QUESTS;
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
	public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
	public static int THREAD_P_EFFECTS;
	public static int THREAD_P_GENERAL;
	public static int THREAD_E_EVENTS;
	public static int GENERAL_PACKET_THREAD_CORE_SIZE;
	public static int IO_PACKET_THREAD_CORE_SIZE;
	public static int GENERAL_THREAD_CORE_SIZE;
	public static int AI_MAX_THREAD;
	public static int EVENT_MAX_THREAD;
	public static int CLIENT_PACKET_QUEUE_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL;
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN;
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	public static boolean ALLOW_DISCARDITEM;
	public static int AUTODESTROY_ITEM_AFTER;
	public static int HERB_AUTO_DESTROY_TIME;
	public static List<Integer> LIST_PROTECTED_ITEMS;
	public static boolean DATABASE_CLEAN_UP;
	public static long CONNECTION_CLOSE_TIME;
	public static int CHAR_STORE_INTERVAL;
	public static boolean LAZY_ITEMS_UPDATE;
	public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	public static boolean SAVE_DROPPED_ITEM;
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	public static boolean PRECISE_DROP_CALCULATION;
	public static boolean MULTIPLE_ITEM_DROP;
	public static boolean FORCE_INVENTORY_UPDATE;
	public static boolean LAZY_CACHE;
	public static boolean CACHE_CHAR_NAMES;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	public static boolean ENABLE_FALLING_DAMAGE;
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	public static boolean MOVE_BASED_KNOWNLIST;
	public static long KNOWNLIST_UPDATE_INTERVAL;
	public static int PEACE_ZONE_MODE;
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean WAREHOUSE_CACHE;
	public static int WAREHOUSE_CACHE_TIME;
	public static boolean ALLOW_REFUND;
	public static boolean ALLOW_MAIL;
	public static boolean ALLOW_ATTACHMENTS;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static int INSTANCE_FINISH_TIME;
	public static boolean RESTORE_PLAYER_INSTANCE;
	public static boolean ALLOW_SUMMON_IN_INSTANCE;
	public static int EJECT_DEAD_PLAYER_TIME;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_RACE;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_RENTPET;
	public static boolean ALLOWFISHING;
	public static boolean ALLOW_BOAT;
	public static int BOAT_BROADCAST_RADIUS;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ALLOW_PET_WALKERS;
	public static boolean SERVER_NEWS;
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static int[] BAN_CHAT_CHANNELS;
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static int ALT_OLY_MAX_BUFFS;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int ALT_OLY_TEAMS;
	public static int ALT_OLY_REG_DISPLAY;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int[][] ALT_OLY_TEAM_REWARD;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
	public static boolean ALT_OLY_LOG_FIGHTS;
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_MIN;
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	public static long ALT_LOTTERY_PRIZE;
	public static long ALT_LOTTERY_TICKET_PRICE;
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	public static long ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
	public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static IllegalActionPunishmentType DEFAULT_PUNISH;
	public static int DEFAULT_PUNISH_PARAM;
	public static boolean ONLY_GM_ITEMS_FREE;
	public static boolean JAIL_IS_PVP;
	public static boolean JAIL_DISABLE_CHAT;
	public static boolean JAIL_DISABLE_TRANSACTION;
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	public static boolean SAVE_GMSPAWN_ON_CUSTOM;
	public static boolean CUSTOM_NPC_DATA;
	public static boolean CUSTOM_TELEPORT_TABLE;
	public static boolean CUSTOM_NPCBUFFER_TABLES;
	public static boolean CUSTOM_SKILLS_LOAD;
	public static boolean CUSTOM_ITEMS_LOAD;
	public static boolean CUSTOM_MULTISELL_LOAD;
	public static boolean CUSTOM_BUYLIST_LOAD;
	public static int ALT_BIRTHDAY_GIFT;
	public static String ALT_BIRTHDAY_MAIL_SUBJECT;
	public static String ALT_BIRTHDAY_MAIL_TEXT;
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static boolean HBCE_FAIR_PLAY;
	public static boolean HELLBOUND_WITHOUT_QUEST;
	public static int PLAYER_MOVEMENT_BLOCK_TIME;
	public static int NORMAL_ENCHANT_COST_MULTIPLIER;
	public static int SAFE_ENCHANT_COST_MULTIPLIER;
	public static boolean BOTREPORT_ENABLE;
	public static String[] BOTREPORT_RESETPOINT_HOUR;
	public static long BOTREPORT_REPORT_DELAY;
	public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;
	
	// --------------------------------------------------
	// FloodProtector Settings
	// --------------------------------------------------
	public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
	public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;
	// --------------------------------------------------
	// L2JMods Settings
	// --------------------------------------------------
	public static boolean L2JMOD_CHAMPION_ENABLE;
	public static boolean L2JMOD_CHAMPION_PASSIVE;
	public static int L2JMOD_CHAMPION_FREQUENCY;
	public static String L2JMOD_CHAMP_TITLE;
	public static int L2JMOD_CHAMP_MIN_LVL;
	public static int L2JMOD_CHAMP_MAX_LVL;
	public static int L2JMOD_CHAMPION_HP;
	public static int L2JMOD_CHAMPION_REWARDS;
	public static float L2JMOD_CHAMPION_ADENAS_REWARDS;
	public static float L2JMOD_CHAMPION_HP_REGEN;
	public static float L2JMOD_CHAMPION_ATK;
	public static float L2JMOD_CHAMPION_SPD_ATK;
	public static int L2JMOD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
	public static int L2JMOD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
	public static int L2JMOD_CHAMPION_REWARD_ID;
	public static int L2JMOD_CHAMPION_REWARD_QTY;
	public static boolean L2JMOD_CHAMPION_ENABLE_VITALITY;
	public static boolean L2JMOD_CHAMPION_ENABLE_IN_INSTANCES;
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_EVENT_IN_INSTANCE;
	public static String TVT_EVENT_INSTANCE_FILE;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	public static int TVT_EVENT_MAX_PARTICIPANTS_PER_IP;
	public static boolean TVT_ALLOW_VOICED_COMMAND;
	public static boolean L2JMOD_ALLOW_WEDDING;
	public static int L2JMOD_WEDDING_PRICE;
	public static boolean L2JMOD_WEDDING_PUNISH_INFIDELITY;
	public static boolean L2JMOD_WEDDING_TELEPORT;
	public static int L2JMOD_WEDDING_TELEPORT_PRICE;
	public static int L2JMOD_WEDDING_TELEPORT_DURATION;
	public static boolean L2JMOD_WEDDING_SAMESEX;
	public static boolean L2JMOD_WEDDING_FORMALWEAR;
	public static int L2JMOD_WEDDING_DIVORCE_COSTS;
	public static boolean L2JMOD_HELLBOUND_STATUS;
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	public static boolean L2JMOD_ENABLE_WAREHOUSESORTING_CLAN;
	public static boolean L2JMOD_ENABLE_WAREHOUSESORTING_PRIVATE;
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	public static boolean OFFLINE_FAME;
	public static boolean L2JMOD_ENABLE_MANA_POTIONS_SUPPORT;
	public static boolean L2JMOD_DISPLAY_SERVER_TIME;
	public static boolean WELCOME_MESSAGE_ENABLED;
	public static String WELCOME_MESSAGE_TEXT;
	public static int WELCOME_MESSAGE_TIME;
	public static boolean L2JMOD_ANTIFEED_ENABLE;
	public static boolean L2JMOD_ANTIFEED_DUALBOX;
	public static boolean L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int L2JMOD_ANTIFEED_INTERVAL;
	public static boolean ANNOUNCE_PK_PVP;
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
	public static String ANNOUNCE_PK_MSG;
	public static String ANNOUNCE_PVP_MSG;
	public static boolean L2JMOD_CHAT_ADMIN;
	public static boolean L2JMOD_MULTILANG_ENABLE;
	public static List<String> L2JMOD_MULTILANG_ALLOWED = new ArrayList<>();
	public static String L2JMOD_MULTILANG_DEFAULT;
	public static boolean L2JMOD_MULTILANG_VOICED_ALLOW;
	public static boolean L2JMOD_MULTILANG_SM_ENABLE;
	public static List<String> L2JMOD_MULTILANG_SM_ALLOWED = new ArrayList<>();
	public static boolean L2JMOD_MULTILANG_NS_ENABLE;
	public static List<String> L2JMOD_MULTILANG_NS_ALLOWED = new ArrayList<>();
	public static boolean L2WALKER_PROTECTION;
	public static boolean L2JMOD_DEBUG_VOICE_COMMAND;
	public static int L2JMOD_DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	public static int L2JMOD_DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	public static int L2JMOD_DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	public static Map<Integer, Integer> L2JMOD_DUALBOX_CHECK_WHITELIST;
	public static boolean L2JMOD_ALLOW_CHANGE_PASSWORD;
	// --------------------------------------------------
	// NPC Settings
	// --------------------------------------------------
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean ALT_ATTACKABLE_NPCS;
	public static boolean ALT_GAME_VIEWNPC;
	public static int MAX_DRIFT_RANGE;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean DEEPBLUE_DROP_RULES_RAID;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_CREST_WITHOUT_QUEST;
	public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
	public static int MIN_NPC_LVL_DMG_PENALTY;
	public static Map<Integer, Float> NPC_DMG_PENALTY;
	public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
	public static int MIN_NPC_LVL_MAGIC_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
	public static int DECAY_TIME_TASK;
	public static int DEFAULT_CORPSE_TIME;
	public static int SPOILED_CORPSE_EXTEND_TIME;
	public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static boolean ALLOW_WYVERN_UPGRADER;
	public static List<Integer> LIST_PET_RENT_NPC;
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_PDEFENCE_MULTIPLIER;
	public static double RAID_MDEFENCE_MULTIPLIER;
	public static double RAID_PATTACK_MULTIPLIER;
	public static double RAID_MATTACK_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	public static int INVENTORY_MAXIMUM_PET;
	public static double PET_HP_REGEN_MULTIPLIER;
	public static double PET_MP_REGEN_MULTIPLIER;
	public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
	public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
	public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
	public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
	
	// --------------------------------------------------
	// PvP Settings
	// --------------------------------------------------
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	// --------------------------------------------------
	// Rate Settings
	// --------------------------------------------------
	public static float RATE_XP;
	public static float RATE_SP;
	public static float RATE_PARTY_XP;
	public static float RATE_PARTY_SP;
	public static float RATE_HB_TRUST_INCREASE;
	public static float RATE_HB_TRUST_DECREASE;
	public static float RATE_EXTRACTABLE;
	public static int RATE_DROP_MANOR;
	public static float RATE_QUEST_DROP;
	public static float RATE_QUEST_REWARD;
	public static float RATE_QUEST_REWARD_XP;
	public static float RATE_QUEST_REWARD_SP;
	public static float RATE_QUEST_REWARD_ADENA;
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	public static float RATE_QUEST_REWARD_POTION;
	public static float RATE_QUEST_REWARD_SCROLL;
	public static float RATE_QUEST_REWARD_RECIPE;
	public static float RATE_QUEST_REWARD_MATERIAL;
	public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_CORPSE_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
	public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
	public static float RATE_CORPSE_DROP_CHANCE_MULTIPLIER;
	public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
	public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_AMOUNT_MULTIPLIER;
	public static Map<Integer, Float> RATE_DROP_CHANCE_MULTIPLIER;
	public static float RATE_KARMA_LOST;
	public static float RATE_KARMA_EXP_LOST;
	public static float RATE_SIEGE_GUARDS_PRICE;
	public static float RATE_DROP_COMMON_HERBS;
	public static float RATE_DROP_HP_HERBS;
	public static float RATE_DROP_MP_HERBS;
	public static float RATE_DROP_SPECIAL_HERBS;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static float PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static float SINEATER_XP_RATE;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	// --------------------------------------------------
	// Seven Signs Settings
	// --------------------------------------------------
	public static boolean ALT_GAME_CASTLE_DAWN;
	public static boolean ALT_GAME_CASTLE_DUSK;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	public static double ALT_SIEGE_DAWN_GATES_PDEF_MULT;
	public static double ALT_SIEGE_DUSK_GATES_PDEF_MULT;
	public static double ALT_SIEGE_DAWN_GATES_MDEF_MULT;
	public static double ALT_SIEGE_DUSK_GATES_MDEF_MULT;
	public static boolean ALT_STRICT_SEVENSIGNS;
	public static boolean ALT_SEVENSIGNS_LAZY_UPDATE;
	public static int SSQ_DAWN_TICKET_QUANTITY;
	public static int SSQ_DAWN_TICKET_PRICE;
	public static int SSQ_DAWN_TICKET_BUNDLE;
	public static int SSQ_MANORS_AGREEMENT_ID;
	public static int SSQ_JOIN_DAWN_ADENA_FEE;
	
	// --------------------------------------------------
	// Server Settings
	// --------------------------------------------------
	public static boolean ENABLE_UPNP;
	public static int PORT_GAME;
	public static int PORT_LOGIN;
	public static String LOGIN_BIND_ADDRESS;
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static String GAMESERVER_HOSTNAME;
	public static String DATABASE_DRIVER;
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIME;
	public static int MAXIMUM_ONLINE_USERS;
	public static String CNAME_TEMPLATE;
	public static String PET_NAME_TEMPLATE;
	public static String CLAN_NAME_TEMPLATE;
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	public static File DATAPACK_ROOT;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static int REQUEST_ID;
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	public static List<Integer> PROTOCOL_LIST;
	public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
	public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;
	
	// --------------------------------------------------
	// MMO Settings
	// --------------------------------------------------
	public static int MMO_SELECTOR_SLEEP_TIME;
	public static int MMO_MAX_SEND_PER_PASS;
	public static int MMO_MAX_READ_PER_PASS;
	public static int MMO_HELPER_BUFFER_COUNT;
	public static boolean MMO_TCP_NODELAY;
	
	// --------------------------------------------------
	// Vitality Settings
	// --------------------------------------------------
	public static boolean ENABLE_VITALITY;
	public static boolean RECOVER_VITALITY_ON_RECONNECT;
	public static boolean ENABLE_DROP_VITALITY_HERBS;
	public static float RATE_VITALITY_LEVEL_1;
	public static float RATE_VITALITY_LEVEL_2;
	public static float RATE_VITALITY_LEVEL_3;
	public static float RATE_VITALITY_LEVEL_4;
	public static float RATE_DROP_VITALITY_HERBS;
	public static float RATE_RECOVERY_VITALITY_PEACE_ZONE;
	public static float RATE_VITALITY_LOST;
	public static float RATE_VITALITY_GAIN;
	public static float RATE_RECOVERY_ON_RECONNECT;
	public static int STARTING_VITALITY_POINTS;
	
	// --------------------------------------------------
	// No classification assigned to the following yet
	// --------------------------------------------------
	public static int MAX_ITEM_IN_PACKET;
	public static boolean CHECK_KNOWN;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static List<String> GAME_SERVER_SUBNETS;
	public static List<String> GAME_SERVER_HOSTS;
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	public static enum IdFactoryType
	{
		Compaction,
		BitSet,
		Stack
	}
	
	public static IdFactoryType IDFACTORY_TYPE;
	public static boolean BAD_ID_CHECKING;
	
	public static double ENCHANT_CHANCE_ELEMENT_STONE;
	public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
	public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
	public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
	public static int[] ENCHANT_BLACKLIST;
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	public static int AUGMENTATION_ACC_SKILL_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION;
	public static int[] RETAIL_LIKE_AUGMENTATION_NG_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_MID_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_TOP_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION_ACCESSORY;
	public static int[] AUGMENTATION_BLACKLIST;
	public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static boolean IS_TELNET_ENABLED;
	public static boolean SHOW_LICENCE;
	public static boolean ACCEPT_NEW_GAMESERVER;
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// GrandBoss Settings
	
	// Antharas
	public static int ANTHARAS_WAIT_TIME;
	public static int ANTHARAS_SPAWN_INTERVAL;
	public static int ANTHARAS_SPAWN_RANDOM;
	
	// Valakas
	public static int VALAKAS_WAIT_TIME;
	public static int VALAKAS_SPAWN_INTERVAL;
	public static int VALAKAS_SPAWN_RANDOM;
	
	// Baium
	public static int BAIUM_SPAWN_INTERVAL;
	public static int BAIUM_SPAWN_RANDOM;
	
	// Core
	public static int CORE_SPAWN_INTERVAL;
	public static int CORE_SPAWN_RANDOM;
	
	// Offen
	public static int ORFEN_SPAWN_INTERVAL;
	public static int ORFEN_SPAWN_RANDOM;
	
	// Queen Ant
	public static int QUEEN_ANT_SPAWN_INTERVAL;
	public static int QUEEN_ANT_SPAWN_RANDOM;
	
	// Beleth
	public static int BELETH_MIN_PLAYERS;
	public static int BELETH_SPAWN_INTERVAL;
	public static int BELETH_SPAWN_RANDOM;
	
	// Gracia Seeds Settings
	public static int SOD_TIAT_KILL_COUNT;
	public static long SOD_STAGE_2_LENGTH;
	
	// chatfilter
	public static List<String> FILTER_LIST;
	
	// Email
	public static String EMAIL_SERVERINFO_NAME;
	public static String EMAIL_SERVERINFO_ADDRESS;
	public static boolean EMAIL_SYS_ENABLED;
	public static String EMAIL_SYS_HOST;
	public static int EMAIL_SYS_PORT;
	public static boolean EMAIL_SYS_SMTP_AUTH;
	public static String EMAIL_SYS_FACTORY;
	public static boolean EMAIL_SYS_FACTORY_CALLBACK;
	public static String EMAIL_SYS_USERNAME;
	public static String EMAIL_SYS_PASSWORD;
	public static String EMAIL_SYS_ADDRESS;
	public static String EMAIL_SYS_SELECTQUERY;
	public static String EMAIL_SYS_DBFIELD;
	
	// Conquerable Halls Settings
	public static int CHS_CLAN_MINLEVEL;
	public static int CHS_MAX_ATTACKERS;
	public static int CHS_MAX_FLAGS_PER_CLAN;
	public static boolean CHS_ENABLE_FAME;
	public static int CHS_FAME_AMOUNT;
	public static int CHS_FAME_FREQUENCY;
	
	// GeoData Settings
	public static int PATHFINDING;
	public static File PATHNODE_DIR;
	public static String PATHFIND_BUFFERS;
	public static float LOW_WEIGHT;
	public static float MEDIUM_WEIGHT;
	public static float HIGH_WEIGHT;
	public static boolean ADVANCED_DIAGONAL_STRATEGY;
	public static float DIAGONAL_WEIGHT;
	public static int MAX_POSTFILTER_PASSES;
	public static boolean DEBUG_PATH;
	public static boolean FORCE_GEODATA;
	public static int COORD_SYNCHRONIZE;
	public static Path GEODATA_PATH;
	public static boolean TRY_LOAD_UNSPECIFIED_REGIONS;
	public static Map<String, Boolean> GEODATA_REGIONS;
	
	/**
	 * This class initializes all global variables for configuration.<br>
	 * If the key doesn't appear in properties file, a default value is set by this class. {@link #CONFIGURATION_FILE} (properties file) for configuring your server.
	 */
	public static void load()
	{
		if (Server.serverMode == Server.MODE_GAMESERVER)
		{
			FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
			FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
			FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
			FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
			FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
			FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
			FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
			FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
			FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
			FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
			FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
			FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
			FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
			FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
			FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
			FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");
			
			final PropertiesParser serverSettings = new PropertiesParser(CONFIGURATION_FILE);
			
			ENABLE_UPNP = serverSettings.getBoolean("EnableUPnP", true);
			GAMESERVER_HOSTNAME = serverSettings.getString("GameserverHostname", "*");
			PORT_GAME = serverSettings.getInt("GameserverPort", 7777);
			
			GAME_SERVER_LOGIN_PORT = serverSettings.getInt("LoginPort", 9014);
			GAME_SERVER_LOGIN_HOST = serverSettings.getString("LoginHost", "127.0.0.1");
			
			REQUEST_ID = serverSettings.getInt("RequestServerID", 0);
			ACCEPT_ALTERNATE_ID = serverSettings.getBoolean("AcceptAlternateID", true);
			
			DATABASE_DRIVER = serverSettings.getString("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL = serverSettings.getString("URL", "jdbc:mysql://localhost/l2jgs");
			DATABASE_LOGIN = serverSettings.getString("Login", "root");
			DATABASE_PASSWORD = serverSettings.getString("Password", "");
			DATABASE_MAX_CONNECTIONS = serverSettings.getInt("MaximumDbConnections", 10);
			DATABASE_MAX_IDLE_TIME = serverSettings.getInt("MaximumDbIdleTime", 0);
			
			try
			{
				DATAPACK_ROOT = new File(serverSettings.getString("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, "Error setting datapack root!", e);
				DATAPACK_ROOT = new File(".");
			}
			
			CNAME_TEMPLATE = serverSettings.getString("CnameTemplate", ".*");
			PET_NAME_TEMPLATE = serverSettings.getString("PetNameTemplate", ".*");
			CLAN_NAME_TEMPLATE = serverSettings.getString("ClanNameTemplate", ".*");
			
			MAX_CHARACTERS_NUMBER_PER_ACCOUNT = serverSettings.getInt("CharMaxNumber", 7);
			MAXIMUM_ONLINE_USERS = serverSettings.getInt("MaximumOnlineUsers", 100);
			
			String[] protocols = serverSettings.getString("AllowedProtocolRevisions", "267;268;271;273").split(";");
			PROTOCOL_LIST = new ArrayList<>(protocols.length);
			for (String protocol : protocols)
			{
				try
				{
					PROTOCOL_LIST.add(Integer.parseInt(protocol.trim()));
				}
				catch (NumberFormatException e)
				{
					_log.log(Level.WARNING, "Wrong config protocol version: " + protocol + ". Skipped.");
				}
			}
			
			// Hosts and Subnets
			IPConfigData ipcd = new IPConfigData();
			GAME_SERVER_SUBNETS = ipcd.getSubnets();
			GAME_SERVER_HOSTS = ipcd.getHosts();
			
			// Load Feature L2Properties file (if exists)
			final PropertiesParser Feature = new PropertiesParser(FEATURE_CONFIG_FILE);
			
			CH_TELE_FEE_RATIO = Feature.getLong("ClanHallTeleportFunctionFeeRatio", 604800000);
			CH_TELE1_FEE = Feature.getInt("ClanHallTeleportFunctionFeeLvl1", 7000);
			CH_TELE2_FEE = Feature.getInt("ClanHallTeleportFunctionFeeLvl2", 14000);
			CH_SUPPORT_FEE_RATIO = Feature.getLong("ClanHallSupportFunctionFeeRatio", 86400000);
			CH_SUPPORT1_FEE = Feature.getInt("ClanHallSupportFeeLvl1", 2500);
			CH_SUPPORT2_FEE = Feature.getInt("ClanHallSupportFeeLvl2", 5000);
			CH_SUPPORT3_FEE = Feature.getInt("ClanHallSupportFeeLvl3", 7000);
			CH_SUPPORT4_FEE = Feature.getInt("ClanHallSupportFeeLvl4", 11000);
			CH_SUPPORT5_FEE = Feature.getInt("ClanHallSupportFeeLvl5", 21000);
			CH_SUPPORT6_FEE = Feature.getInt("ClanHallSupportFeeLvl6", 36000);
			CH_SUPPORT7_FEE = Feature.getInt("ClanHallSupportFeeLvl7", 37000);
			CH_SUPPORT8_FEE = Feature.getInt("ClanHallSupportFeeLvl8", 52000);
			CH_MPREG_FEE_RATIO = Feature.getLong("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
			CH_MPREG1_FEE = Feature.getInt("ClanHallMpRegenerationFeeLvl1", 2000);
			CH_MPREG2_FEE = Feature.getInt("ClanHallMpRegenerationFeeLvl2", 3750);
			CH_MPREG3_FEE = Feature.getInt("ClanHallMpRegenerationFeeLvl3", 6500);
			CH_MPREG4_FEE = Feature.getInt("ClanHallMpRegenerationFeeLvl4", 13750);
			CH_MPREG5_FEE = Feature.getInt("ClanHallMpRegenerationFeeLvl5", 20000);
			CH_HPREG_FEE_RATIO = Feature.getLong("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
			CH_HPREG1_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl1", 700);
			CH_HPREG2_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl2", 800);
			CH_HPREG3_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl3", 1000);
			CH_HPREG4_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl4", 1166);
			CH_HPREG5_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl5", 1500);
			CH_HPREG6_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl6", 1750);
			CH_HPREG7_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl7", 2000);
			CH_HPREG8_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl8", 2250);
			CH_HPREG9_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl9", 2500);
			CH_HPREG10_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl10", 3250);
			CH_HPREG11_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl11", 3270);
			CH_HPREG12_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl12", 4250);
			CH_HPREG13_FEE = Feature.getInt("ClanHallHpRegenerationFeeLvl13", 5166);
			CH_EXPREG_FEE_RATIO = Feature.getLong("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
			CH_EXPREG1_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl1", 3000);
			CH_EXPREG2_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl2", 6000);
			CH_EXPREG3_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl3", 9000);
			CH_EXPREG4_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl4", 15000);
			CH_EXPREG5_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl5", 21000);
			CH_EXPREG6_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl6", 23330);
			CH_EXPREG7_FEE = Feature.getInt("ClanHallExpRegenerationFeeLvl7", 30000);
			CH_ITEM_FEE_RATIO = Feature.getLong("ClanHallItemCreationFunctionFeeRatio", 86400000);
			CH_ITEM1_FEE = Feature.getInt("ClanHallItemCreationFunctionFeeLvl1", 30000);
			CH_ITEM2_FEE = Feature.getInt("ClanHallItemCreationFunctionFeeLvl2", 70000);
			CH_ITEM3_FEE = Feature.getInt("ClanHallItemCreationFunctionFeeLvl3", 140000);
			CH_CURTAIN_FEE_RATIO = Feature.getLong("ClanHallCurtainFunctionFeeRatio", 604800000);
			CH_CURTAIN1_FEE = Feature.getInt("ClanHallCurtainFunctionFeeLvl1", 2000);
			CH_CURTAIN2_FEE = Feature.getInt("ClanHallCurtainFunctionFeeLvl2", 2500);
			CH_FRONT_FEE_RATIO = Feature.getLong("ClanHallFrontPlatformFunctionFeeRatio", 259200000);
			CH_FRONT1_FEE = Feature.getInt("ClanHallFrontPlatformFunctionFeeLvl1", 1300);
			CH_FRONT2_FEE = Feature.getInt("ClanHallFrontPlatformFunctionFeeLvl2", 4000);
			CH_BUFF_FREE = Feature.getBoolean("AltClanHallMpBuffFree", false);
			SIEGE_HOUR_LIST = new ArrayList<>();
			for (String hour : Feature.getString("SiegeHourList", "").split(","))
			{
				if (Util.isDigit(hour))
				{
					SIEGE_HOUR_LIST.add(Integer.parseInt(hour));
				}
			}
			CS_TELE_FEE_RATIO = Feature.getLong("CastleTeleportFunctionFeeRatio", 604800000);
			CS_TELE1_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl1", 1000);
			CS_TELE2_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl2", 10000);
			CS_SUPPORT_FEE_RATIO = Feature.getLong("CastleSupportFunctionFeeRatio", 604800000);
			CS_SUPPORT1_FEE = Feature.getInt("CastleSupportFeeLvl1", 49000);
			CS_SUPPORT2_FEE = Feature.getInt("CastleSupportFeeLvl2", 120000);
			CS_MPREG_FEE_RATIO = Feature.getLong("CastleMpRegenerationFunctionFeeRatio", 604800000);
			CS_MPREG1_FEE = Feature.getInt("CastleMpRegenerationFeeLvl1", 45000);
			CS_MPREG2_FEE = Feature.getInt("CastleMpRegenerationFeeLvl2", 65000);
			CS_HPREG_FEE_RATIO = Feature.getLong("CastleHpRegenerationFunctionFeeRatio", 604800000);
			CS_HPREG1_FEE = Feature.getInt("CastleHpRegenerationFeeLvl1", 12000);
			CS_HPREG2_FEE = Feature.getInt("CastleHpRegenerationFeeLvl2", 20000);
			CS_EXPREG_FEE_RATIO = Feature.getLong("CastleExpRegenerationFunctionFeeRatio", 604800000);
			CS_EXPREG1_FEE = Feature.getInt("CastleExpRegenerationFeeLvl1", 63000);
			CS_EXPREG2_FEE = Feature.getInt("CastleExpRegenerationFeeLvl2", 70000);
			
			OUTER_DOOR_UPGRADE_PRICE2 = Feature.getInt("OuterDoorUpgradePriceLvl2", 3000000);
			OUTER_DOOR_UPGRADE_PRICE3 = Feature.getInt("OuterDoorUpgradePriceLvl3", 4000000);
			OUTER_DOOR_UPGRADE_PRICE5 = Feature.getInt("OuterDoorUpgradePriceLvl5", 5000000);
			INNER_DOOR_UPGRADE_PRICE2 = Feature.getInt("InnerDoorUpgradePriceLvl2", 750000);
			INNER_DOOR_UPGRADE_PRICE3 = Feature.getInt("InnerDoorUpgradePriceLvl3", 900000);
			INNER_DOOR_UPGRADE_PRICE5 = Feature.getInt("InnerDoorUpgradePriceLvl5", 1000000);
			WALL_UPGRADE_PRICE2 = Feature.getInt("WallUpgradePriceLvl2", 1600000);
			WALL_UPGRADE_PRICE3 = Feature.getInt("WallUpgradePriceLvl3", 1800000);
			WALL_UPGRADE_PRICE5 = Feature.getInt("WallUpgradePriceLvl5", 2000000);
			TRAP_UPGRADE_PRICE1 = Feature.getInt("TrapUpgradePriceLvl1", 3000000);
			TRAP_UPGRADE_PRICE2 = Feature.getInt("TrapUpgradePriceLvl2", 4000000);
			TRAP_UPGRADE_PRICE3 = Feature.getInt("TrapUpgradePriceLvl3", 5000000);
			TRAP_UPGRADE_PRICE4 = Feature.getInt("TrapUpgradePriceLvl4", 6000000);
			
			FS_TELE_FEE_RATIO = Feature.getLong("FortressTeleportFunctionFeeRatio", 604800000);
			FS_TELE1_FEE = Feature.getInt("FortressTeleportFunctionFeeLvl1", 1000);
			FS_TELE2_FEE = Feature.getInt("FortressTeleportFunctionFeeLvl2", 10000);
			FS_SUPPORT_FEE_RATIO = Feature.getLong("FortressSupportFunctionFeeRatio", 86400000);
			FS_SUPPORT1_FEE = Feature.getInt("FortressSupportFeeLvl1", 7000);
			FS_SUPPORT2_FEE = Feature.getInt("FortressSupportFeeLvl2", 17000);
			FS_MPREG_FEE_RATIO = Feature.getLong("FortressMpRegenerationFunctionFeeRatio", 86400000);
			FS_MPREG1_FEE = Feature.getInt("FortressMpRegenerationFeeLvl1", 6500);
			FS_MPREG2_FEE = Feature.getInt("FortressMpRegenerationFeeLvl2", 9300);
			FS_HPREG_FEE_RATIO = Feature.getLong("FortressHpRegenerationFunctionFeeRatio", 86400000);
			FS_HPREG1_FEE = Feature.getInt("FortressHpRegenerationFeeLvl1", 2000);
			FS_HPREG2_FEE = Feature.getInt("FortressHpRegenerationFeeLvl2", 3500);
			FS_EXPREG_FEE_RATIO = Feature.getLong("FortressExpRegenerationFunctionFeeRatio", 86400000);
			FS_EXPREG1_FEE = Feature.getInt("FortressExpRegenerationFeeLvl1", 9000);
			FS_EXPREG2_FEE = Feature.getInt("FortressExpRegenerationFeeLvl2", 10000);
			FS_UPDATE_FRQ = Feature.getInt("FortressPeriodicUpdateFrequency", 360);
			FS_BLOOD_OATH_COUNT = Feature.getInt("FortressBloodOathCount", 1);
			FS_MAX_SUPPLY_LEVEL = Feature.getInt("FortressMaxSupplyLevel", 6);
			FS_FEE_FOR_CASTLE = Feature.getInt("FortressFeeForCastle", 25000);
			FS_MAX_OWN_TIME = Feature.getInt("FortressMaximumOwnTime", 168);
			
			ALT_GAME_CASTLE_DAWN = Feature.getBoolean("AltCastleForDawn", true);
			ALT_GAME_CASTLE_DUSK = Feature.getBoolean("AltCastleForDusk", true);
			ALT_GAME_REQUIRE_CLAN_CASTLE = Feature.getBoolean("AltRequireClanCastle", false);
			ALT_FESTIVAL_MIN_PLAYER = Feature.getInt("AltFestivalMinPlayer", 5);
			ALT_MAXIMUM_PLAYER_CONTRIB = Feature.getInt("AltMaxPlayerContrib", 1000000);
			ALT_FESTIVAL_MANAGER_START = Feature.getLong("AltFestivalManagerStart", 120000);
			ALT_FESTIVAL_LENGTH = Feature.getLong("AltFestivalLength", 1080000);
			ALT_FESTIVAL_CYCLE_LENGTH = Feature.getLong("AltFestivalCycleLength", 2280000);
			ALT_FESTIVAL_FIRST_SPAWN = Feature.getLong("AltFestivalFirstSpawn", 120000);
			ALT_FESTIVAL_FIRST_SWARM = Feature.getLong("AltFestivalFirstSwarm", 300000);
			ALT_FESTIVAL_SECOND_SPAWN = Feature.getLong("AltFestivalSecondSpawn", 540000);
			ALT_FESTIVAL_SECOND_SWARM = Feature.getLong("AltFestivalSecondSwarm", 720000);
			ALT_FESTIVAL_CHEST_SPAWN = Feature.getLong("AltFestivalChestSpawn", 900000);
			ALT_SIEGE_DAWN_GATES_PDEF_MULT = Feature.getDouble("AltDawnGatesPdefMult", 1.1);
			ALT_SIEGE_DUSK_GATES_PDEF_MULT = Feature.getDouble("AltDuskGatesPdefMult", 0.8);
			ALT_SIEGE_DAWN_GATES_MDEF_MULT = Feature.getDouble("AltDawnGatesMdefMult", 1.1);
			ALT_SIEGE_DUSK_GATES_MDEF_MULT = Feature.getDouble("AltDuskGatesMdefMult", 0.8);
			ALT_STRICT_SEVENSIGNS = Feature.getBoolean("StrictSevenSigns", true);
			ALT_SEVENSIGNS_LAZY_UPDATE = Feature.getBoolean("AltSevenSignsLazyUpdate", true);
			
			SSQ_DAWN_TICKET_QUANTITY = Feature.getInt("SevenSignsDawnTicketQuantity", 300);
			SSQ_DAWN_TICKET_PRICE = Feature.getInt("SevenSignsDawnTicketPrice", 1000);
			SSQ_DAWN_TICKET_BUNDLE = Feature.getInt("SevenSignsDawnTicketBundle", 10);
			SSQ_MANORS_AGREEMENT_ID = Feature.getInt("SevenSignsManorsAgreementId", 6388);
			SSQ_JOIN_DAWN_ADENA_FEE = Feature.getInt("SevenSignsJoinDawnFee", 50000);
			
			TAKE_FORT_POINTS = Feature.getInt("TakeFortPoints", 200);
			LOOSE_FORT_POINTS = Feature.getInt("LooseFortPoints", 0);
			TAKE_CASTLE_POINTS = Feature.getInt("TakeCastlePoints", 1500);
			LOOSE_CASTLE_POINTS = Feature.getInt("LooseCastlePoints", 3000);
			CASTLE_DEFENDED_POINTS = Feature.getInt("CastleDefendedPoints", 750);
			FESTIVAL_WIN_POINTS = Feature.getInt("FestivalOfDarknessWin", 200);
			HERO_POINTS = Feature.getInt("HeroPoints", 1000);
			ROYAL_GUARD_COST = Feature.getInt("CreateRoyalGuardCost", 5000);
			KNIGHT_UNIT_COST = Feature.getInt("CreateKnightUnitCost", 10000);
			KNIGHT_REINFORCE_COST = Feature.getInt("ReinforceKnightUnitCost", 5000);
			BALLISTA_POINTS = Feature.getInt("KillBallistaPoints", 30);
			BLOODALLIANCE_POINTS = Feature.getInt("BloodAlliancePoints", 500);
			BLOODOATH_POINTS = Feature.getInt("BloodOathPoints", 200);
			KNIGHTSEPAULETTE_POINTS = Feature.getInt("KnightsEpaulettePoints", 20);
			REPUTATION_SCORE_PER_KILL = Feature.getInt("ReputationScorePerKill", 1);
			JOIN_ACADEMY_MIN_REP_SCORE = Feature.getInt("CompleteAcademyMinPoints", 190);
			JOIN_ACADEMY_MAX_REP_SCORE = Feature.getInt("CompleteAcademyMaxPoints", 650);
			RAID_RANKING_1ST = Feature.getInt("1stRaidRankingPoints", 1250);
			RAID_RANKING_2ND = Feature.getInt("2ndRaidRankingPoints", 900);
			RAID_RANKING_3RD = Feature.getInt("3rdRaidRankingPoints", 700);
			RAID_RANKING_4TH = Feature.getInt("4thRaidRankingPoints", 600);
			RAID_RANKING_5TH = Feature.getInt("5thRaidRankingPoints", 450);
			RAID_RANKING_6TH = Feature.getInt("6thRaidRankingPoints", 350);
			RAID_RANKING_7TH = Feature.getInt("7thRaidRankingPoints", 300);
			RAID_RANKING_8TH = Feature.getInt("8thRaidRankingPoints", 200);
			RAID_RANKING_9TH = Feature.getInt("9thRaidRankingPoints", 150);
			RAID_RANKING_10TH = Feature.getInt("10thRaidRankingPoints", 100);
			RAID_RANKING_UP_TO_50TH = Feature.getInt("UpTo50thRaidRankingPoints", 25);
			RAID_RANKING_UP_TO_100TH = Feature.getInt("UpTo100thRaidRankingPoints", 12);
			CLAN_LEVEL_6_COST = Feature.getInt("ClanLevel6Cost", 5000);
			CLAN_LEVEL_7_COST = Feature.getInt("ClanLevel7Cost", 10000);
			CLAN_LEVEL_8_COST = Feature.getInt("ClanLevel8Cost", 20000);
			CLAN_LEVEL_9_COST = Feature.getInt("ClanLevel9Cost", 40000);
			CLAN_LEVEL_10_COST = Feature.getInt("ClanLevel10Cost", 40000);
			CLAN_LEVEL_11_COST = Feature.getInt("ClanLevel11Cost", 75000);
			CLAN_LEVEL_6_REQUIREMENT = Feature.getInt("ClanLevel6Requirement", 30);
			CLAN_LEVEL_7_REQUIREMENT = Feature.getInt("ClanLevel7Requirement", 50);
			CLAN_LEVEL_8_REQUIREMENT = Feature.getInt("ClanLevel8Requirement", 80);
			CLAN_LEVEL_9_REQUIREMENT = Feature.getInt("ClanLevel9Requirement", 120);
			CLAN_LEVEL_10_REQUIREMENT = Feature.getInt("ClanLevel10Requirement", 140);
			CLAN_LEVEL_11_REQUIREMENT = Feature.getInt("ClanLevel11Requirement", 170);
			ALLOW_WYVERN_ALWAYS = Feature.getBoolean("AllowRideWyvernAlways", false);
			ALLOW_WYVERN_DURING_SIEGE = Feature.getBoolean("AllowRideWyvernDuringSiege", true);
			// :Zersia
			// Load Custom Properties file (if exists)
			final PropertiesParser CustomSettings = new PropertiesParser(CUSTOM_CONFIG_FILE);
			ALLOW_HUMAN = CustomSettings.getBoolean("AllowHuman", true);
			ALLOW_ELF = CustomSettings.getBoolean("AllowElf", true);
			ALLOW_DARKELF = CustomSettings.getBoolean("AllowDarkElf", true);
			ALLOW_ORC = CustomSettings.getBoolean("AllowOrc", true);
			ALLOW_DWARF = CustomSettings.getBoolean("AllowDwarf", true);
			ALLOW_KAMAEL = CustomSettings.getBoolean("AllowKamael", true);
			// ----------------------------------------------------------------
			ENABLE_CHAR_TITLE = CustomSettings.getBoolean("EnableCharTitle", true);
			CHAR_TITLE = CustomSettings.getString("CharTitle", "Welcome!");
			// ----------------------------------------------------------------
			CUSTOM_STARTING_LOC = CustomSettings.getBoolean("CustomStartingLocation", false);
			CUSTOM_STARTING_LOC_X = CustomSettings.getInt("CustomStartingLocX", 82698);
			CUSTOM_STARTING_LOC_Y = CustomSettings.getInt("CustomStartingLocY", 148638);
			CUSTOM_STARTING_LOC_Z = CustomSettings.getInt("CustomStartingLocZ", -3473);
			// ----------------------------------------------------------------
			RAIDBOSS_KILLED_ANNOUNCE = CustomSettings.getBoolean("RaidBossKilledAnnounce", false);
			RAIDBOSS_KILLED_ANNOUNCE_MSG = CustomSettings.getString("RaidBossKilledAnnounceMsg", "$player $raidboss kills.");
			GRANDBOSS_KILLED_ANNOUNCE = CustomSettings.getBoolean("GrandBossKilledAnnounce", false);
			GRANDBOSS_KILLED_ANNOUNCE_MSG = CustomSettings.getString("GrandBossKilledAnnounceMsg", "$player $grandboss kills.");
			ANNOUNCE_SPAWN_RAIDBOSS = CustomSettings.getBoolean("AnnounceSpawnRaidBoss", false);
			ANNOUNCE_SPAWN_RAIDBOSS_MSG = CustomSettings.getString("AnnounceSpawnRaidBossMsg", "$raidboss Spawn.");
			// ----------------------------------------------------------------
			ENABLE_PC_BANG = CustomSettings.getBoolean("EnabledPcBang", true);
			MAX_PC_BANG_POINTS = CustomSettings.getInt("MaxPcBangPoints", 2100000000);
			
			if (MAX_PC_BANG_POINTS < 0)
			{
				MAX_PC_BANG_POINTS = 0;
			}
			RANDOM_PC_BANG_POINT = CustomSettings.getBoolean("AcquisitionPointsRandom", false);
			ENABLE_DOUBLE_PC_BANG_POINTS = CustomSettings.getBoolean("DoublingAcquisitionPoints", true);
			DOUBLE_PC_BANG_POINTS_CHANCE = CustomSettings.getInt("DoublingAcquisitionPointsChance", 1);
			if ((DOUBLE_PC_BANG_POINTS_CHANCE < 0) || (DOUBLE_PC_BANG_POINTS_CHANCE > 100))
			{
				DOUBLE_PC_BANG_POINTS_CHANCE = 1;
			}
			PC_BANG_POINT_RATE = CustomSettings.getDouble("AcquisitionPointsRate", 1.0);
			if (PC_BANG_POINT_RATE < 0)
			{
				PC_BANG_POINT_RATE = 1;
			}
			PC_BANG_REWARD_LOW_EXP_KILLS = CustomSettings.getBoolean("RewardLowExpKills", true);
			PC_BANG_LOW_EXP_KILLS_CHANCE = CustomSettings.getInt("RewardLowExpKillsChance", 50);
			if ((PC_BANG_LOW_EXP_KILLS_CHANCE < 0) || (PC_BANG_LOW_EXP_KILLS_CHANCE > 100))
			{
				PC_BANG_LOW_EXP_KILLS_CHANCE = 1;
			}
			// ----------------------------------------------------------------
			CUSTOM_STARTING_PC_BANG_POINT = CustomSettings.getInt("CustomStartingPcBangPoints", 0);
			if ((CUSTOM_STARTING_PC_BANG_POINT < 0) || (CUSTOM_STARTING_PC_BANG_POINT > 2100000000))
			{
				CUSTOM_STARTING_PC_BANG_POINT = 0;
			}
			CUSTOM_STARTING_GAME_POINT = CustomSettings.getInt("CustomStartingGamePoints", 0);
			if (CUSTOM_STARTING_GAME_POINT < 0)
			{
				CUSTOM_STARTING_GAME_POINT = 0;
			}
			// ----------------------------------------------------------------
			PARTYGO_MINLEVEL = CustomSettings.getInt("PartyGoMinLv", 1);
			PARTYGO_MAXLEVEL = CustomSettings.getInt("PartyGoMaxLv", 85);
			PARTYGO_ITEMID = CustomSettings.getInt("PartyGoItemId", 8615);
			PARTYGO_ITEMCOUNT = CustomSettings.getInt("PartyGoItemCount", 1);
			PARTYGO_ENABLE = CustomSettings.getBoolean("EnabledPartyGo", false);
			// ----------------------------------------------------------------
			ANTIBOT_ENABLE = CustomSettings.getBoolean("EnabledAntiBot", false);
			ANTIBOT_LEVEL = CustomSettings.getInt("AntiBotLevel", 1);
			ANTIBOT_INPUTTIME = CustomSettings.getInt("AntiBotInputTime", 1);
			ANTIBOT_MSG = CustomSettings.getString("AntiBotMsg", "");
			// ----------------------------------------------------------------
			CHGSEX_ITEMID = CustomSettings.getInt("ChgSexItemId", 57);
			CHGSEX_ITEMCOUNT = CustomSettings.getInt("ChgSexItemCount", 1000000);
			CHGSEX_ENABLE = CustomSettings.getBoolean("EnabledChgSex", false);
			// ----------------------------------------------------------------
			SELFENCHANT_ENABLE = CustomSettings.getBoolean("EnabledSelfEnchant", false);
			WEAPONMAX_ENCHANT = CustomSettings.getInt("WeaponMaxEnchant", 15);
			WEAPON_ITEM = CustomSettings.getInt("WeaponItem", 57);
			WEAPON_ITEMCOUNT = CustomSettings.getInt("WeaponItemCount", 3000000);
			ARMORMAX_ENCHANT = CustomSettings.getInt("ArmorMaxEnchant", 15);
			ARMOR_ITEM = CustomSettings.getInt("ArmorItem", 57);
			ARMOR_ITEMCOUNT = CustomSettings.getInt("ArmorItemCount", 1000000);
			JEWELSMAX_ENCHANT = CustomSettings.getInt("JewelsMaxEnchant", 15);
			JEWELS_ITEM = CustomSettings.getInt("JewelsItem", 57);
			JEWELS_ITEMCOUNT = CustomSettings.getInt("JewelsItemCount", 1000000);
			BELTSHIRTMAX_ENCHANT = CustomSettings.getInt("BeltShirtMaxEnchant", 15);
			BELTSHIRT_ITEM = CustomSettings.getInt("BeltShirtItem", 57);
			BELTSHIRT_ITEMCOUNT = CustomSettings.getInt("BeltShirtItemCount", 1000000);
			// ----------------------------------------------------------------
			MAXENCHANT_ENABLE = CustomSettings.getBoolean("EnabledMaxEnchant", false);
			MAXWEAPONMAX_ENCHANT = CustomSettings.getInt("MaxWeaponMaxEnchant", 35);
			MAXWEAPON_ITEM = CustomSettings.getInt("MaxWeaponItem", 99761);
			MAXWEAPON_ITEMCOUNT = CustomSettings.getInt("MaxWeaponItemCount", 300);
			MAXARMORMAX_ENCHANT = CustomSettings.getInt("MaxArmorMaxEnchant", 35);
			MAXARMOR_ITEM = CustomSettings.getInt("MaxArmorItem", 99761);
			MAXARMOR_ITEMCOUNT = CustomSettings.getInt("MaxArmorItemCount", 100);
			MAXJEWELSMAX_ENCHANT = CustomSettings.getInt("MaxJewelsMaxEnchant", 35);
			MAXJEWELS_ITEM = CustomSettings.getInt("MaxJewelsItem", 99761);
			MAXJEWELS_ITEMCOUNT = CustomSettings.getInt("MaxJewelsItemCount", 100);
			MAXBELTSHIRTMAX_ENCHANT = CustomSettings.getInt("MaxBeltShirtMaxEnchant", 35);
			MAXBELTSHIRT_ITEM = CustomSettings.getInt("MaxBeltShirtItem", 99761);
			MAXBELTSHIRT_ITEMCOUNT = CustomSettings.getInt("MaxBeltShirtItemCount", 100);
			// ----------------------------------------------------------------
			LOTOENCHANT_ENABLE = CustomSettings.getBoolean("EnabledLotoEnchant", false);
			LOTOWEAPONMAX_ENCHANT = CustomSettings.getInt("LotoWeaponMaxEnchant", 35);
			LOTOWEAPON_ITEM = CustomSettings.getInt("LotoWeaponItem", 99761);
			LOTOWEAPON_ITEMCOUNT = CustomSettings.getInt("LotoWeaponItemCount", 30);
			LOTOARMORMAX_ENCHANT = CustomSettings.getInt("LotoArmorMaxEnchant", 35);
			LOTOARMOR_ITEM = CustomSettings.getInt("LotoArmorItem", 99761);
			LOTOARMOR_ITEMCOUNT = CustomSettings.getInt("LotoArmorItemCount", 10);
			LOTOJEWELSMAX_ENCHANT = CustomSettings.getInt("LotoJewelsMaxEnchant", 35);
			LOTOJEWELS_ITEM = CustomSettings.getInt("LotoJewelsItem", 99761);
			LOTOJEWELS_ITEMCOUNT = CustomSettings.getInt("LotoJewelsItemCount", 10);
			LOTOBELTSHIRTMAX_ENCHANT = CustomSettings.getInt("LotoBeltShirtMaxEnchant", 35);
			LOTOBELTSHIRT_ITEM = CustomSettings.getInt("LotoBeltShirtItem", 99761);
			LOTOBELTSHIRT_ITEMCOUNT = CustomSettings.getInt("LotoBeltShirtItemCount", 10);
			// ----------------------------------------------------------------
			EVENTENCHANT_ENABLE = CustomSettings.getBoolean("EventEnchantEnabled", false);
			EVENT_ENCHANT = CustomSettings.getInt("EventEnchant", 2);
			EVENT_ENCHANTITEM = CustomSettings.getInt("EventEnchantItem", 99760);
			EVENT_ENCHANTITEMCOUNT = CustomSettings.getInt("EventEnchantItemCount", 1);
			// ----------------------------------------------------------------
			EVENTGAMBLE_ENABLE = CustomSettings.getBoolean("EventGambleEnabled", false);
			GAMBLERANKSET = CustomSettings.getInt("EventGambleRankSet", 10);
			GAMBLEITEM = CustomSettings.getInt("EventGambleItem", 99759);
			GAMBLEITEMCOUNT = CustomSettings.getInt("EventGambleItemCount", 1);
			GAMBLERANKITEM01 = CustomSettings.getInt("EventGambleRankItem01", 57);
			GAMBLERANKITEMCOUNT01 = CustomSettings.getInt("EventGambleRankItemCount01", 1);
			GAMBLERANKITEM02 = CustomSettings.getInt("EventGambleRankItem02", 57);
			GAMBLERANKITEMCOUNT02 = CustomSettings.getInt("EventGambleRankItemCount02", 1);
			GAMBLERANKITEM03 = CustomSettings.getInt("EventGambleRankItem03", 57);
			GAMBLERANKITEMCOUNT03 = CustomSettings.getInt("EventGambleRankItemCount03", 1);
			GAMBLERANKITEM04 = CustomSettings.getInt("EventGambleRankItem04", 57);
			GAMBLERANKITEMCOUNT04 = CustomSettings.getInt("EventGambleRankItemCount04", 1);
			GAMBLERANKITEM05 = CustomSettings.getInt("EventGambleRankItem05", 57);
			GAMBLERANKITEMCOUNT05 = CustomSettings.getInt("EventGambleRankItemCount05", 1);
			GAMBLERANKITEM06 = CustomSettings.getInt("EventGambleRankItem06", 57);
			GAMBLERANKITEMCOUNT06 = CustomSettings.getInt("EventGambleRankItemCount06", 1);
			GAMBLERANKITEM07 = CustomSettings.getInt("EventGambleRankItem07", 57);
			GAMBLERANKITEMCOUNT07 = CustomSettings.getInt("EventGambleRankItemCount07", 1);
			GAMBLERANKITEM08 = CustomSettings.getInt("EventGambleRankItem08", 57);
			GAMBLERANKITEMCOUNT08 = CustomSettings.getInt("EventGambleRankItemCount08", 1);
			GAMBLERANKITEM09 = CustomSettings.getInt("EventGambleRankItem09", 57);
			GAMBLERANKITEMCOUNT09 = CustomSettings.getInt("EventGambleRankItemCount09", 1);
			GAMBLERANKITEM10 = CustomSettings.getInt("EventGambleRankItem10", 57);
			GAMBLERANKITEMCOUNT10 = CustomSettings.getInt("EventGambleRankItemCount10", 1);
			// ----------------------------------------------------------------
			GMGIFT_ENABLE = CustomSettings.getBoolean("GmGiftEnabled", false);
			GMGIFT_ITEMID = CustomSettings.getInt("GmGiftItemId", 57);
			GMGIFT_ITEMCOUNT = CustomSettings.getInt("GmGiftItemCount", 100);
			GMGIFT_RUNNING_TIME = CustomSettings.getInt("GmGiftRunningTime", 1800);
			GMGIFT_MSG = CustomSettings.getString("GmGiftMsg", "");
			ONLINEUSER_MSG = CustomSettings.getString("OnLineUserMsg", "");
			// ----------------------------------------------------------------
			SATURDAY_RATE_XP = CustomSettings.getInt("SatRateXp", 2);
			SATURDAY_RATE_SP = CustomSettings.getInt("SatRateSp", 2);
			WEEK_RATE_CODE1 = CustomSettings.getInt("WeekCode1", 1);
			WEEK_RATE_CODE2 = CustomSettings.getInt("WeekCode2", 7);
			// ----------------------------------------------------------------
			CLANPOINT = CustomSettings.getInt("ClanPoint", 100);
			CLANPOINT_MSG = CustomSettings.getString("ClanPointMsg", "");
			// ----------------------------------------------------------------
			GAMEPOINT = CustomSettings.getInt("GamePoint", 100);
			GAMEPOINT_MSG = CustomSettings.getString("GamePointMsg", "");
			// ----------------------------------------------------------------
			ZERSIA_CONFIG = CustomSettings.getString("ZersiaHostname", "");
			ZERSIA_LICENCE = CustomSettings.getString("ZersiaLicence", "");
			// ----------------------------------------------------------------
			SOLOINSTANCE_ITEMID1 = CustomSettings.getInt("SoloInstanceItemId1", 57);
			SOLOINSTANCE_ITEMID2 = CustomSettings.getInt("SoloInstanceItemId2", 57);
			SOLOINSTANCE_ITEMID3 = CustomSettings.getInt("SoloInstanceItemId3", 57);
			SOLOINSTANCE_ITEMID4 = CustomSettings.getInt("SoloInstanceItemId4", 57);
			SOLOINSTANCE_ITEMCNT1 = CustomSettings.getInt("SoloInstanceItemCnt1", 1);
			SOLOINSTANCE_ITEMCNT2 = CustomSettings.getInt("SoloInstanceItemCnt2", 1);
			SOLOINSTANCE_ITEMCNT3 = CustomSettings.getInt("SoloInstanceItemCnt3", 1);
			SOLOINSTANCE_ITEMCNT4 = CustomSettings.getInt("SoloInstanceItemCnt4", 1);
			SOLOINSTANCE_EXITING_LOC_X = CustomSettings.getInt("SoloInstanceExitingLocX", 82698);
			SOLOINSTANCE_EXITING_LOC_Y = CustomSettings.getInt("SoloInstanceExitingLocY", 148638);
			SOLOINSTANCE_EXITING_LOC_Z = CustomSettings.getInt("SoloInstanceExitingLocZ", -3473);
			
			// Load Custom Message Properties file (if exists)
			final PropertiesParser CustomMessageSettings = new PropertiesParser(CUSTOM_MESSAGE_FILE);
			CUSTOM_MSG_1001 = CustomMessageSettings.getString("CustomMsg1001", "");
			CUSTOM_MSG_1002 = CustomMessageSettings.getString("CustomMsg1002", "");
			CUSTOM_MSG_1003 = CustomMessageSettings.getString("CustomMsg1003", "");
			CUSTOM_MSG_1004 = CustomMessageSettings.getString("CustomMsg1004", "");
			CUSTOM_MSG_1005 = CustomMessageSettings.getString("CustomMsg1005", "");
			CUSTOM_MSG_1006 = CustomMessageSettings.getString("CustomMsg1006", "");
			CUSTOM_MSG_1007 = CustomMessageSettings.getString("CustomMsg1007", "");
			CUSTOM_MSG_1008 = CustomMessageSettings.getString("CustomMsg1008", "");
			CUSTOM_MSG_1009 = CustomMessageSettings.getString("CustomMsg1009", "");
			CUSTOM_MSG_1010 = CustomMessageSettings.getString("CustomMsg1010", "");
			CUSTOM_MSG_1011 = CustomMessageSettings.getString("CustomMsg1011", "");
			CUSTOM_MSG_1012 = CustomMessageSettings.getString("CustomMsg1012", "");
			CUSTOM_MSG_1013 = CustomMessageSettings.getString("CustomMsg1013", "");
			CUSTOM_MSG_1014 = CustomMessageSettings.getString("CustomMsg1014", "");
			CUSTOM_MSG_1015 = CustomMessageSettings.getString("CustomMsg1015", "");
			CUSTOM_MSG_1016 = CustomMessageSettings.getString("CustomMsg1016", "");
			CUSTOM_MSG_1017 = CustomMessageSettings.getString("CustomMsg1017", "");
			CUSTOM_MSG_1018 = CustomMessageSettings.getString("CustomMsg1018", "");
			CUSTOM_MSG_1019 = CustomMessageSettings.getString("CustomMsg1019", "");
			CUSTOM_MSG_1020 = CustomMessageSettings.getString("CustomMsg1020", "");
			CUSTOM_MSG_1021 = CustomMessageSettings.getString("CustomMsg1021", "");
			CUSTOM_MSG_1022 = CustomMessageSettings.getString("CustomMsg1022", "");
			CUSTOM_MSG_1023 = CustomMessageSettings.getString("CustomMsg1023", "");
			CUSTOM_MSG_1024 = CustomMessageSettings.getString("CustomMsg1024", "");
			CUSTOM_MSG_1025 = CustomMessageSettings.getString("CustomMsg1025", "");
			CUSTOM_MSG_1026 = CustomMessageSettings.getString("CustomMsg1026", "");
			CUSTOM_MSG_1027 = CustomMessageSettings.getString("CustomMsg1027", "");
			CUSTOM_MSG_1028 = CustomMessageSettings.getString("CustomMsg1028", "");
			CUSTOM_MSG_1029 = CustomMessageSettings.getString("CustomMsg1029", "");
			CUSTOM_MSG_1030 = CustomMessageSettings.getString("CustomMsg1030", "");
			CUSTOM_MSG_1031 = CustomMessageSettings.getString("CustomMsg1031", "");
			CUSTOM_MSG_1032 = CustomMessageSettings.getString("CustomMsg1032", "");
			CUSTOM_MSG_1033 = CustomMessageSettings.getString("CustomMsg1033", "");
			CUSTOM_MSG_1034 = CustomMessageSettings.getString("CustomMsg1034", "");
			CUSTOM_MSG_1035 = CustomMessageSettings.getString("CustomMsg1035", "");
			CUSTOM_MSG_1036 = CustomMessageSettings.getString("CustomMsg1036", "");
			CUSTOM_MSG_1037 = CustomMessageSettings.getString("CustomMsg1037", "");
			CUSTOM_MSG_1038 = CustomMessageSettings.getString("CustomMsg1038", "");
			CUSTOM_MSG_1039 = CustomMessageSettings.getString("CustomMsg1039", "");
			CUSTOM_MSG_1040 = CustomMessageSettings.getString("CustomMsg1040", "");
			CUSTOM_MSG_1041 = CustomMessageSettings.getString("CustomMsg1041", "");
			CUSTOM_MSG_1042 = CustomMessageSettings.getString("CustomMsg1042", "");
			CUSTOM_MSG_1043 = CustomMessageSettings.getString("CustomMsg1043", "");
			CUSTOM_MSG_1044 = CustomMessageSettings.getString("CustomMsg1044", "");
			CUSTOM_MSG_1045 = CustomMessageSettings.getString("CustomMsg1045", "");
			CUSTOM_MSG_1046 = CustomMessageSettings.getString("CustomMsg1046", "");
			CUSTOM_MSG_1047 = CustomMessageSettings.getString("CustomMsg1047", "");
			CUSTOM_MSG_1048 = CustomMessageSettings.getString("CustomMsg1048", "");
			CUSTOM_MSG_1049 = CustomMessageSettings.getString("CustomMsg1049", "");
			CUSTOM_MSG_1050 = CustomMessageSettings.getString("CustomMsg1050", "");
			CUSTOM_MSG_1051 = CustomMessageSettings.getString("CustomMsg1051", "");
			CUSTOM_MSG_1052 = CustomMessageSettings.getString("CustomMsg1052", "");
			CUSTOM_MSG_1053 = CustomMessageSettings.getString("CustomMsg1053", "");
			CUSTOM_MSG_1054 = CustomMessageSettings.getString("CustomMsg1054", "");
			CUSTOM_MSG_1055 = CustomMessageSettings.getString("CustomMsg1055", "");
			CUSTOM_MSG_1056 = CustomMessageSettings.getString("CustomMsg1056", "");
			CUSTOM_MSG_1057 = CustomMessageSettings.getString("CustomMsg1057", "");
			CUSTOM_MSG_1058 = CustomMessageSettings.getString("CustomMsg1058", "");
			CUSTOM_MSG_1059 = CustomMessageSettings.getString("CustomMsg1059", "");
			CUSTOM_MSG_1060 = CustomMessageSettings.getString("CustomMsg1060", "");
			CUSTOM_MSG_1061 = CustomMessageSettings.getString("CustomMsg1061", "");
			CUSTOM_MSG_1062 = CustomMessageSettings.getString("CustomMsg1062", "");
			CUSTOM_MSG_1063 = CustomMessageSettings.getString("CustomMsg1063", "");
			CUSTOM_MSG_1064 = CustomMessageSettings.getString("CustomMsg1064", "");
			CUSTOM_MSG_1065 = CustomMessageSettings.getString("CustomMsg1065", "");
			CUSTOM_MSG_1066 = CustomMessageSettings.getString("CustomMsg1066", "");
			CUSTOM_MSG_1067 = CustomMessageSettings.getString("CustomMsg1067", "");
			CUSTOM_MSG_1068 = CustomMessageSettings.getString("CustomMsg1068", "");
			CUSTOM_MSG_1069 = CustomMessageSettings.getString("CustomMsg1069", "");
			CUSTOM_MSG_1070 = CustomMessageSettings.getString("CustomMsg1070", "");
			CUSTOM_MSG_1071 = CustomMessageSettings.getString("CustomMsg1071", "");
			CUSTOM_MSG_1072 = CustomMessageSettings.getString("CustomMsg1072", "");
			CUSTOM_MSG_1073 = CustomMessageSettings.getString("CustomMsg1073", "");
			CUSTOM_MSG_1074 = CustomMessageSettings.getString("CustomMsg1074", "");
			CUSTOM_MSG_1075 = CustomMessageSettings.getString("CustomMsg1075", "");
			CUSTOM_MSG_1076 = CustomMessageSettings.getString("CustomMsg1076", "");
			CUSTOM_MSG_1077 = CustomMessageSettings.getString("CustomMsg1077", "");
			CUSTOM_MSG_1078 = CustomMessageSettings.getString("CustomMsg1078", "");
			CUSTOM_MSG_1079 = CustomMessageSettings.getString("CustomMsg1079", "");
			CUSTOM_MSG_1080 = CustomMessageSettings.getString("CustomMsg1080", "");
			CUSTOM_MSG_1081 = CustomMessageSettings.getString("CustomMsg1081", "");
			CUSTOM_MSG_1082 = CustomMessageSettings.getString("CustomMsg1082", "");
			CUSTOM_MSG_1083 = CustomMessageSettings.getString("CustomMsg1083", "");
			CUSTOM_MSG_1084 = CustomMessageSettings.getString("CustomMsg1084", "");
			CUSTOM_MSG_1085 = CustomMessageSettings.getString("CustomMsg1085", "");
			CUSTOM_MSG_1086 = CustomMessageSettings.getString("CustomMsg1086", "");
			CUSTOM_MSG_1087 = CustomMessageSettings.getString("CustomMsg1087", "");
			CUSTOM_MSG_1088 = CustomMessageSettings.getString("CustomMsg1088", "");
			CUSTOM_MSG_1089 = CustomMessageSettings.getString("CustomMsg1089", "");
			CUSTOM_MSG_1090 = CustomMessageSettings.getString("CustomMsg1090", "");
			CUSTOM_MSG_1091 = CustomMessageSettings.getString("CustomMsg1091", "");
			CUSTOM_MSG_1092 = CustomMessageSettings.getString("CustomMsg1092", "");
			CUSTOM_MSG_1093 = CustomMessageSettings.getString("CustomMsg1093", "");
			CUSTOM_MSG_1094 = CustomMessageSettings.getString("CustomMsg1094", "");
			CUSTOM_MSG_1095 = CustomMessageSettings.getString("CustomMsg1095", "");
			CUSTOM_MSG_1096 = CustomMessageSettings.getString("CustomMsg1096", "");
			CUSTOM_MSG_1097 = CustomMessageSettings.getString("CustomMsg1097", "");
			CUSTOM_MSG_1098 = CustomMessageSettings.getString("CustomMsg1098", "");
			CUSTOM_MSG_1099 = CustomMessageSettings.getString("CustomMsg1099", "");
			CUSTOM_MSG_1100 = CustomMessageSettings.getString("CustomMsg1100", "");
			CUSTOM_MSG_1101 = CustomMessageSettings.getString("CustomMsg1101", "");
			CUSTOM_MSG_1102 = CustomMessageSettings.getString("CustomMsg1102", "");
			CUSTOM_MSG_1103 = CustomMessageSettings.getString("CustomMsg1103", "");
			CUSTOM_MSG_1104 = CustomMessageSettings.getString("CustomMsg1104", "");
			CUSTOM_MSG_1105 = CustomMessageSettings.getString("CustomMsg1105", "");
			CUSTOM_MSG_1106 = CustomMessageSettings.getString("CustomMsg1106", "");
			CUSTOM_MSG_1107 = CustomMessageSettings.getString("CustomMsg1107", "");
			CUSTOM_MSG_1108 = CustomMessageSettings.getString("CustomMsg1108", "");
			CUSTOM_MSG_1109 = CustomMessageSettings.getString("CustomMsg1109", "");
			CUSTOM_MSG_1110 = CustomMessageSettings.getString("CustomMsg1110", "");
			CUSTOM_MSG_1111 = CustomMessageSettings.getString("CustomMsg1111", "");
			CUSTOM_MSG_1112 = CustomMessageSettings.getString("CustomMsg1112", "");
			CUSTOM_MSG_1113 = CustomMessageSettings.getString("CustomMsg1113", "");
			CUSTOM_MSG_1114 = CustomMessageSettings.getString("CustomMsg1114", "");
			CUSTOM_MSG_1115 = CustomMessageSettings.getString("CustomMsg1115", "");
			CUSTOM_MSG_1116 = CustomMessageSettings.getString("CustomMsg1116", "");
			CUSTOM_MSG_1117 = CustomMessageSettings.getString("CustomMsg1117", "");
			CUSTOM_MSG_1118 = CustomMessageSettings.getString("CustomMsg1118", "");
			CUSTOM_MSG_1119 = CustomMessageSettings.getString("CustomMsg1119", "");
			CUSTOM_MSG_1120 = CustomMessageSettings.getString("CustomMsg1120", "");
			CUSTOM_MSG_1121 = CustomMessageSettings.getString("CustomMsg1121", "");
			CUSTOM_MSG_1122 = CustomMessageSettings.getString("CustomMsg1122", "");
			CUSTOM_MSG_1123 = CustomMessageSettings.getString("CustomMsg1123", "");
			CUSTOM_MSG_1124 = CustomMessageSettings.getString("CustomMsg1124", "");
			CUSTOM_MSG_1125 = CustomMessageSettings.getString("CustomMsg1125", "");
			CUSTOM_MSG_1126 = CustomMessageSettings.getString("CustomMsg1126", "");
			CUSTOM_MSG_1127 = CustomMessageSettings.getString("CustomMsg1127", "");
			CUSTOM_MSG_1128 = CustomMessageSettings.getString("CustomMsg1128", "");
			CUSTOM_MSG_1129 = CustomMessageSettings.getString("CustomMsg1129", "");
			CUSTOM_MSG_1130 = CustomMessageSettings.getString("CustomMsg1130", "");
			CUSTOM_MSG_1131 = CustomMessageSettings.getString("CustomMsg1131", "");
			CUSTOM_MSG_1132 = CustomMessageSettings.getString("CustomMsg1132", "");
			CUSTOM_MSG_1133 = CustomMessageSettings.getString("CustomMsg1133", "");
			CUSTOM_MSG_1134 = CustomMessageSettings.getString("CustomMsg1134", "");
			CUSTOM_MSG_1135 = CustomMessageSettings.getString("CustomMsg1135", "");
			CUSTOM_MSG_1136 = CustomMessageSettings.getString("CustomMsg1136", "");
			CUSTOM_MSG_1137 = CustomMessageSettings.getString("CustomMsg1137", "");
			CUSTOM_MSG_1138 = CustomMessageSettings.getString("CustomMsg1138", "");
			CUSTOM_MSG_1139 = CustomMessageSettings.getString("CustomMsg1139", "");
			CUSTOM_MSG_1140 = CustomMessageSettings.getString("CustomMsg1140", "");
			CUSTOM_MSG_1141 = CustomMessageSettings.getString("CustomMsg1141", "");
			CUSTOM_MSG_1142 = CustomMessageSettings.getString("CustomMsg1142", "");
			CUSTOM_MSG_1143 = CustomMessageSettings.getString("CustomMsg1143", "");
			CUSTOM_MSG_1144 = CustomMessageSettings.getString("CustomMsg1144", "");
			CUSTOM_MSG_1145 = CustomMessageSettings.getString("CustomMsg1145", "");
			CUSTOM_MSG_1146 = CustomMessageSettings.getString("CustomMsg1146", "");
			CUSTOM_MSG_1147 = CustomMessageSettings.getString("CustomMsg1147", "");
			CUSTOM_MSG_1148 = CustomMessageSettings.getString("CustomMsg1148", "");
			CUSTOM_MSG_1149 = CustomMessageSettings.getString("CustomMsg1149", "");
			CUSTOM_MSG_1150 = CustomMessageSettings.getString("CustomMsg1150", "");
			CUSTOM_MSG_1151 = CustomMessageSettings.getString("CustomMsg1151", "");
			CUSTOM_MSG_1152 = CustomMessageSettings.getString("CustomMsg1152", "");
			CUSTOM_MSG_1153 = CustomMessageSettings.getString("CustomMsg1153", "");
			CUSTOM_MSG_1154 = CustomMessageSettings.getString("CustomMsg1154", "");
			CUSTOM_MSG_1155 = CustomMessageSettings.getString("CustomMsg1155", "");
			CUSTOM_MSG_1156 = CustomMessageSettings.getString("CustomMsg1156", "");
			CUSTOM_MSG_1157 = CustomMessageSettings.getString("CustomMsg1157", "");
			CUSTOM_MSG_1158 = CustomMessageSettings.getString("CustomMsg1158", "");
			CUSTOM_MSG_1159 = CustomMessageSettings.getString("CustomMsg1159", "");
			CUSTOM_MSG_1160 = CustomMessageSettings.getString("CustomMsg1160", "");
			CUSTOM_MSG_1161 = CustomMessageSettings.getString("CustomMsg1161", "");
			CUSTOM_MSG_1162 = CustomMessageSettings.getString("CustomMsg1162", "");
			CUSTOM_MSG_1163 = CustomMessageSettings.getString("CustomMsg1163", "");
			CUSTOM_MSG_1164 = CustomMessageSettings.getString("CustomMsg1164", "");
			CUSTOM_MSG_1165 = CustomMessageSettings.getString("CustomMsg1165", "");
			CUSTOM_MSG_1166 = CustomMessageSettings.getString("CustomMsg1166", "");
			CUSTOM_MSG_1167 = CustomMessageSettings.getString("CustomMsg1167", "");
			CUSTOM_MSG_1168 = CustomMessageSettings.getString("CustomMsg1168", "");
			CUSTOM_MSG_1169 = CustomMessageSettings.getString("CustomMsg1169", "");
			CUSTOM_MSG_1170 = CustomMessageSettings.getString("CustomMsg1170", "");
			CUSTOM_MSG_1171 = CustomMessageSettings.getString("CustomMsg1171", "");
			CUSTOM_MSG_1172 = CustomMessageSettings.getString("CustomMsg1172", "");
			CUSTOM_MSG_1173 = CustomMessageSettings.getString("CustomMsg1173", "");
			CUSTOM_MSG_1174 = CustomMessageSettings.getString("CustomMsg1174", "");
			CUSTOM_MSG_1175 = CustomMessageSettings.getString("CustomMsg1175", "");
			CUSTOM_MSG_1176 = CustomMessageSettings.getString("CustomMsg1176", "");
			CUSTOM_MSG_1177 = CustomMessageSettings.getString("CustomMsg1177", "");
			CUSTOM_MSG_1178 = CustomMessageSettings.getString("CustomMsg1178", "");
			CUSTOM_MSG_1179 = CustomMessageSettings.getString("CustomMsg1179", "");
			CUSTOM_MSG_1180 = CustomMessageSettings.getString("CustomMsg1180", "");
			CUSTOM_MSG_1181 = CustomMessageSettings.getString("CustomMsg1181", "");
			CUSTOM_MSG_1182 = CustomMessageSettings.getString("CustomMsg1182", "");
			CUSTOM_MSG_1183 = CustomMessageSettings.getString("CustomMsg1183", "");
			CUSTOM_MSG_1184 = CustomMessageSettings.getString("CustomMsg1184", "");
			CUSTOM_MSG_1185 = CustomMessageSettings.getString("CustomMsg1185", "");
			CUSTOM_MSG_1186 = CustomMessageSettings.getString("CustomMsg1186", "");
			CUSTOM_MSG_1187 = CustomMessageSettings.getString("CustomMsg1187", "");
			CUSTOM_MSG_1188 = CustomMessageSettings.getString("CustomMsg1188", "");
			CUSTOM_MSG_1189 = CustomMessageSettings.getString("CustomMsg1189", "");
			CUSTOM_MSG_1190 = CustomMessageSettings.getString("CustomMsg1190", "");
			CUSTOM_MSG_1191 = CustomMessageSettings.getString("CustomMsg1191", "");
			CUSTOM_MSG_1192 = CustomMessageSettings.getString("CustomMsg1192", "");
			CUSTOM_MSG_1193 = CustomMessageSettings.getString("CustomMsg1193", "");
			CUSTOM_MSG_1194 = CustomMessageSettings.getString("CustomMsg1194", "");
			CUSTOM_MSG_1195 = CustomMessageSettings.getString("CustomMsg1195", "");
			CUSTOM_MSG_1196 = CustomMessageSettings.getString("CustomMsg1196", "");
			CUSTOM_MSG_1197 = CustomMessageSettings.getString("CustomMsg1197", "");
			CUSTOM_MSG_1198 = CustomMessageSettings.getString("CustomMsg1198", "");
			CUSTOM_MSG_1199 = CustomMessageSettings.getString("CustomMsg1199", "");
			CUSTOM_MSG_1200 = CustomMessageSettings.getString("CustomMsg1200", "");
			CUSTOM_MSG_1201 = CustomMessageSettings.getString("CustomMsg1201", "");
			CUSTOM_MSG_1202 = CustomMessageSettings.getString("CustomMsg1202", "");
			CUSTOM_MSG_1203 = CustomMessageSettings.getString("CustomMsg1203", "");
			CUSTOM_MSG_1204 = CustomMessageSettings.getString("CustomMsg1204", "");
			CUSTOM_MSG_1205 = CustomMessageSettings.getString("CustomMsg1205", "");
			CUSTOM_MSG_1206 = CustomMessageSettings.getString("CustomMsg1206", "");
			CUSTOM_MSG_1207 = CustomMessageSettings.getString("CustomMsg1207", "");
			CUSTOM_MSG_1208 = CustomMessageSettings.getString("CustomMsg1208", "");
			CUSTOM_MSG_1209 = CustomMessageSettings.getString("CustomMsg1209", "");
			CUSTOM_MSG_1210 = CustomMessageSettings.getString("CustomMsg1210", "");
			CUSTOM_MSG_1211 = CustomMessageSettings.getString("CustomMsg1211", "");
			CUSTOM_MSG_1212 = CustomMessageSettings.getString("CustomMsg1212", "");
			CUSTOM_MSG_1213 = CustomMessageSettings.getString("CustomMsg1213", "");
			CUSTOM_MSG_1214 = CustomMessageSettings.getString("CustomMsg1214", "");
			CUSTOM_MSG_1215 = CustomMessageSettings.getString("CustomMsg1215", "");
			CUSTOM_MSG_1216 = CustomMessageSettings.getString("CustomMsg1216", "");
			CUSTOM_MSG_1217 = CustomMessageSettings.getString("CustomMsg1217", "");
			CUSTOM_MSG_1218 = CustomMessageSettings.getString("CustomMsg1218", "");
			CUSTOM_MSG_1219 = CustomMessageSettings.getString("CustomMsg1219", "");
			CUSTOM_MSG_1220 = CustomMessageSettings.getString("CustomMsg1220", "");
			CUSTOM_MSG_1221 = CustomMessageSettings.getString("CustomMsg1221", "");
			CUSTOM_MSG_1222 = CustomMessageSettings.getString("CustomMsg1222", "");
			CUSTOM_MSG_1223 = CustomMessageSettings.getString("CustomMsg1223", "");
			CUSTOM_MSG_1224 = CustomMessageSettings.getString("CustomMsg1224", "");
			CUSTOM_MSG_1225 = CustomMessageSettings.getString("CustomMsg1225", "");
			CUSTOM_MSG_1226 = CustomMessageSettings.getString("CustomMsg1226", "");
			CUSTOM_MSG_1227 = CustomMessageSettings.getString("CustomMsg1227", "");
			CUSTOM_MSG_1228 = CustomMessageSettings.getString("CustomMsg1228", "");
			CUSTOM_MSG_1229 = CustomMessageSettings.getString("CustomMsg1229", "");
			CUSTOM_MSG_1230 = CustomMessageSettings.getString("CustomMsg1230", "");
			CUSTOM_MSG_1231 = CustomMessageSettings.getString("CustomMsg1231", "");
			CUSTOM_MSG_1232 = CustomMessageSettings.getString("CustomMsg1232", "");
			CUSTOM_MSG_1233 = CustomMessageSettings.getString("CustomMsg1233", "");
			CUSTOM_MSG_1234 = CustomMessageSettings.getString("CustomMsg1234", "");
			CUSTOM_MSG_1235 = CustomMessageSettings.getString("CustomMsg1235", "");
			CUSTOM_MSG_1236 = CustomMessageSettings.getString("CustomMsg1236", "");
			CUSTOM_MSG_1237 = CustomMessageSettings.getString("CustomMsg1237", "");
			CUSTOM_MSG_1238 = CustomMessageSettings.getString("CustomMsg1238", "");
			CUSTOM_MSG_1239 = CustomMessageSettings.getString("CustomMsg1239", "");
			CUSTOM_MSG_1240 = CustomMessageSettings.getString("CustomMsg1240", "");
			CUSTOM_MSG_1241 = CustomMessageSettings.getString("CustomMsg1241", "");
			CUSTOM_MSG_1242 = CustomMessageSettings.getString("CustomMsg1242", "");
			CUSTOM_MSG_1243 = CustomMessageSettings.getString("CustomMsg1243", "");
			CUSTOM_MSG_1244 = CustomMessageSettings.getString("CustomMsg1244", "");
			CUSTOM_MSG_1245 = CustomMessageSettings.getString("CustomMsg1245", "");
			CUSTOM_MSG_1246 = CustomMessageSettings.getString("CustomMsg1246", "");
			CUSTOM_MSG_1247 = CustomMessageSettings.getString("CustomMsg1247", "");
			CUSTOM_MSG_1248 = CustomMessageSettings.getString("CustomMsg1248", "");
			CUSTOM_MSG_1249 = CustomMessageSettings.getString("CustomMsg1249", "");
			CUSTOM_MSG_1250 = CustomMessageSettings.getString("CustomMsg1250", "");
			CUSTOM_MSG_1251 = CustomMessageSettings.getString("CustomMsg1251", "");
			CUSTOM_MSG_1252 = CustomMessageSettings.getString("CustomMsg1252", "");
			CUSTOM_MSG_1253 = CustomMessageSettings.getString("CustomMsg1253", "");
			CUSTOM_MSG_1254 = CustomMessageSettings.getString("CustomMsg1254", "");
			CUSTOM_MSG_1255 = CustomMessageSettings.getString("CustomMsg1255", "");
			CUSTOM_MSG_1256 = CustomMessageSettings.getString("CustomMsg1256", "");
			CUSTOM_MSG_1257 = CustomMessageSettings.getString("CustomMsg1257", "");
			CUSTOM_MSG_1258 = CustomMessageSettings.getString("CustomMsg1258", "");
			CUSTOM_MSG_1259 = CustomMessageSettings.getString("CustomMsg1259", "");
			CUSTOM_MSG_1260 = CustomMessageSettings.getString("CustomMsg1260", "");
			CUSTOM_MSG_1261 = CustomMessageSettings.getString("CustomMsg1261", "");
			CUSTOM_MSG_1262 = CustomMessageSettings.getString("CustomMsg1262", "");
			CUSTOM_MSG_1263 = CustomMessageSettings.getString("CustomMsg1263", "");
			CUSTOM_MSG_1264 = CustomMessageSettings.getString("CustomMsg1264", "");
			CUSTOM_MSG_1265 = CustomMessageSettings.getString("CustomMsg1265", "");
			CUSTOM_MSG_1266 = CustomMessageSettings.getString("CustomMsg1266", "");
			CUSTOM_MSG_1267 = CustomMessageSettings.getString("CustomMsg1267", "");
			CUSTOM_MSG_1268 = CustomMessageSettings.getString("CustomMsg1268", "");
			CUSTOM_MSG_1269 = CustomMessageSettings.getString("CustomMsg1269", "");
			CUSTOM_MSG_1270 = CustomMessageSettings.getString("CustomMsg1270", "");
			CUSTOM_MSG_1271 = CustomMessageSettings.getString("CustomMsg1271", "");
			CUSTOM_MSG_1272 = CustomMessageSettings.getString("CustomMsg1272", "");
			CUSTOM_MSG_1273 = CustomMessageSettings.getString("CustomMsg1273", "");
			CUSTOM_MSG_1274 = CustomMessageSettings.getString("CustomMsg1274", "");
			CUSTOM_MSG_1275 = CustomMessageSettings.getString("CustomMsg1275", "");
			CUSTOM_MSG_1276 = CustomMessageSettings.getString("CustomMsg1276", "");
			CUSTOM_MSG_1277 = CustomMessageSettings.getString("CustomMsg1277", "");
			CUSTOM_MSG_1278 = CustomMessageSettings.getString("CustomMsg1278", "");
			CUSTOM_MSG_1279 = CustomMessageSettings.getString("CustomMsg1279", "");
			CUSTOM_MSG_1280 = CustomMessageSettings.getString("CustomMsg1280", "");
			CUSTOM_MSG_1281 = CustomMessageSettings.getString("CustomMsg1281", "");
			CUSTOM_MSG_1282 = CustomMessageSettings.getString("CustomMsg1282", "");
			CUSTOM_MSG_1283 = CustomMessageSettings.getString("CustomMsg1283", "");
			CUSTOM_MSG_1284 = CustomMessageSettings.getString("CustomMsg1284", "");
			CUSTOM_MSG_1285 = CustomMessageSettings.getString("CustomMsg1285", "");
			CUSTOM_MSG_1286 = CustomMessageSettings.getString("CustomMsg1286", "");
			CUSTOM_MSG_1287 = CustomMessageSettings.getString("CustomMsg1287", "");
			CUSTOM_MSG_1288 = CustomMessageSettings.getString("CustomMsg1288", "");
			CUSTOM_MSG_1289 = CustomMessageSettings.getString("CustomMsg1289", "");
			CUSTOM_MSG_1290 = CustomMessageSettings.getString("CustomMsg1290", "");
			CUSTOM_MSG_1291 = CustomMessageSettings.getString("CustomMsg1291", "");
			CUSTOM_MSG_1292 = CustomMessageSettings.getString("CustomMsg1292", "");
			CUSTOM_MSG_1293 = CustomMessageSettings.getString("CustomMsg1293", "");
			CUSTOM_MSG_1294 = CustomMessageSettings.getString("CustomMsg1294", "");
			CUSTOM_MSG_1295 = CustomMessageSettings.getString("CustomMsg1295", "");
			CUSTOM_MSG_1296 = CustomMessageSettings.getString("CustomMsg1296", "");
			CUSTOM_MSG_1297 = CustomMessageSettings.getString("CustomMsg1297", "");
			CUSTOM_MSG_1298 = CustomMessageSettings.getString("CustomMsg1298", "");
			CUSTOM_MSG_1299 = CustomMessageSettings.getString("CustomMsg1299", "");
			CUSTOM_MSG_1300 = CustomMessageSettings.getString("CustomMsg1300", "");
			CUSTOM_MSG_1301 = CustomMessageSettings.getString("CustomMsg1301", "");
			CUSTOM_MSG_1302 = CustomMessageSettings.getString("CustomMsg1302", "");
			CUSTOM_MSG_1303 = CustomMessageSettings.getString("CustomMsg1303", "");
			CUSTOM_MSG_1304 = CustomMessageSettings.getString("CustomMsg1304", "");
			CUSTOM_MSG_1305 = CustomMessageSettings.getString("CustomMsg1305", "");
			CUSTOM_MSG_1306 = CustomMessageSettings.getString("CustomMsg1306", "");
			CUSTOM_MSG_1307 = CustomMessageSettings.getString("CustomMsg1307", "");
			CUSTOM_MSG_1308 = CustomMessageSettings.getString("CustomMsg1308", "");
			CUSTOM_MSG_1309 = CustomMessageSettings.getString("CustomMsg1309", "");
			CUSTOM_MSG_1310 = CustomMessageSettings.getString("CustomMsg1310", "");
			CUSTOM_MSG_1311 = CustomMessageSettings.getString("CustomMsg1311", "");
			CUSTOM_MSG_1312 = CustomMessageSettings.getString("CustomMsg1312", "");
			CUSTOM_MSG_1313 = CustomMessageSettings.getString("CustomMsg1313", "");
			CUSTOM_MSG_1314 = CustomMessageSettings.getString("CustomMsg1314", "");
			CUSTOM_MSG_1315 = CustomMessageSettings.getString("CustomMsg1315", "");
			CUSTOM_MSG_1316 = CustomMessageSettings.getString("CustomMsg1316", "");
			CUSTOM_MSG_1317 = CustomMessageSettings.getString("CustomMsg1317", "");
			CUSTOM_MSG_1318 = CustomMessageSettings.getString("CustomMsg1318", "");
			CUSTOM_MSG_1319 = CustomMessageSettings.getString("CustomMsg1319", "");
			CUSTOM_MSG_1320 = CustomMessageSettings.getString("CustomMsg1320", "");
			CUSTOM_MSG_1321 = CustomMessageSettings.getString("CustomMsg1321", "");
			CUSTOM_MSG_1322 = CustomMessageSettings.getString("CustomMsg1322", "");
			CUSTOM_MSG_1323 = CustomMessageSettings.getString("CustomMsg1323", "");
			CUSTOM_MSG_1324 = CustomMessageSettings.getString("CustomMsg1324", "");
			CUSTOM_MSG_1325 = CustomMessageSettings.getString("CustomMsg1325", "");
			CUSTOM_MSG_1326 = CustomMessageSettings.getString("CustomMsg1326", "");
			CUSTOM_MSG_1327 = CustomMessageSettings.getString("CustomMsg1327", "");
			CUSTOM_MSG_1328 = CustomMessageSettings.getString("CustomMsg1328", "");
			CUSTOM_MSG_1329 = CustomMessageSettings.getString("CustomMsg1329", "");
			CUSTOM_MSG_1330 = CustomMessageSettings.getString("CustomMsg1330", "");
			CUSTOM_MSG_1331 = CustomMessageSettings.getString("CustomMsg1331", "");
			CUSTOM_MSG_1332 = CustomMessageSettings.getString("CustomMsg1332", "");
			CUSTOM_MSG_1333 = CustomMessageSettings.getString("CustomMsg1333", "");
			CUSTOM_MSG_1334 = CustomMessageSettings.getString("CustomMsg1334", "");
			CUSTOM_MSG_1335 = CustomMessageSettings.getString("CustomMsg1335", "");
			CUSTOM_MSG_1336 = CustomMessageSettings.getString("CustomMsg1336", "");
			CUSTOM_MSG_1337 = CustomMessageSettings.getString("CustomMsg1337", "");
			CUSTOM_MSG_1338 = CustomMessageSettings.getString("CustomMsg1338", "");
			CUSTOM_MSG_1339 = CustomMessageSettings.getString("CustomMsg1339", "");
			CUSTOM_MSG_1340 = CustomMessageSettings.getString("CustomMsg1340", "");
			CUSTOM_MSG_1341 = CustomMessageSettings.getString("CustomMsg1341", "");
			CUSTOM_MSG_1342 = CustomMessageSettings.getString("CustomMsg1342", "");
			CUSTOM_MSG_1343 = CustomMessageSettings.getString("CustomMsg1343", "");
			CUSTOM_MSG_1344 = CustomMessageSettings.getString("CustomMsg1344", "");
			CUSTOM_MSG_1345 = CustomMessageSettings.getString("CustomMsg1345", "");
			CUSTOM_MSG_1346 = CustomMessageSettings.getString("CustomMsg1346", "");
			CUSTOM_MSG_1347 = CustomMessageSettings.getString("CustomMsg1347", "");
			CUSTOM_MSG_1348 = CustomMessageSettings.getString("CustomMsg1348", "");
			CUSTOM_MSG_1349 = CustomMessageSettings.getString("CustomMsg1349", "");
			CUSTOM_MSG_1350 = CustomMessageSettings.getString("CustomMsg1350", "");
			CUSTOM_MSG_1351 = CustomMessageSettings.getString("CustomMsg1351", "");
			CUSTOM_MSG_1352 = CustomMessageSettings.getString("CustomMsg1352", "");
			CUSTOM_MSG_1353 = CustomMessageSettings.getString("CustomMsg1353", "");
			CUSTOM_MSG_1354 = CustomMessageSettings.getString("CustomMsg1354", "");
			CUSTOM_MSG_1355 = CustomMessageSettings.getString("CustomMsg1355", "");
			CUSTOM_MSG_1356 = CustomMessageSettings.getString("CustomMsg1356", "");
			CUSTOM_MSG_1357 = CustomMessageSettings.getString("CustomMsg1357", "");
			CUSTOM_MSG_1358 = CustomMessageSettings.getString("CustomMsg1358", "");
			CUSTOM_MSG_1359 = CustomMessageSettings.getString("CustomMsg1359", "");
			CUSTOM_MSG_1360 = CustomMessageSettings.getString("CustomMsg1360", "");
			CUSTOM_MSG_1361 = CustomMessageSettings.getString("CustomMsg1361", "");
			CUSTOM_MSG_1362 = CustomMessageSettings.getString("CustomMsg1362", "");
			CUSTOM_MSG_1363 = CustomMessageSettings.getString("CustomMsg1363", "");
			CUSTOM_MSG_1364 = CustomMessageSettings.getString("CustomMsg1364", "");
			CUSTOM_MSG_1365 = CustomMessageSettings.getString("CustomMsg1365", "");
			CUSTOM_MSG_1366 = CustomMessageSettings.getString("CustomMsg1366", "");
			CUSTOM_MSG_1367 = CustomMessageSettings.getString("CustomMsg1367", "");
			CUSTOM_MSG_1368 = CustomMessageSettings.getString("CustomMsg1368", "");
			CUSTOM_MSG_1369 = CustomMessageSettings.getString("CustomMsg1369", "");
			CUSTOM_MSG_1370 = CustomMessageSettings.getString("CustomMsg1370", "");
			CUSTOM_MSG_1371 = CustomMessageSettings.getString("CustomMsg1371", "");
			CUSTOM_MSG_1372 = CustomMessageSettings.getString("CustomMsg1372", "");
			CUSTOM_MSG_1373 = CustomMessageSettings.getString("CustomMsg1373", "");
			CUSTOM_MSG_1374 = CustomMessageSettings.getString("CustomMsg1374", "");
			CUSTOM_MSG_1375 = CustomMessageSettings.getString("CustomMsg1375", "");
			CUSTOM_MSG_1376 = CustomMessageSettings.getString("CustomMsg1376", "");
			CUSTOM_MSG_1377 = CustomMessageSettings.getString("CustomMsg1377", "");
			CUSTOM_MSG_1378 = CustomMessageSettings.getString("CustomMsg1378", "");
			CUSTOM_MSG_1379 = CustomMessageSettings.getString("CustomMsg1379", "");
			CUSTOM_MSG_1380 = CustomMessageSettings.getString("CustomMsg1380", "");
			CUSTOM_MSG_1381 = CustomMessageSettings.getString("CustomMsg1381", "");
			CUSTOM_MSG_1382 = CustomMessageSettings.getString("CustomMsg1382", "");
			CUSTOM_MSG_1383 = CustomMessageSettings.getString("CustomMsg1383", "");
			CUSTOM_MSG_1384 = CustomMessageSettings.getString("CustomMsg1384", "");
			CUSTOM_MSG_1385 = CustomMessageSettings.getString("CustomMsg1385", "");
			CUSTOM_MSG_1386 = CustomMessageSettings.getString("CustomMsg1386", "");
			CUSTOM_MSG_1387 = CustomMessageSettings.getString("CustomMsg1387", "");
			CUSTOM_MSG_1388 = CustomMessageSettings.getString("CustomMsg1388", "");
			CUSTOM_MSG_1389 = CustomMessageSettings.getString("CustomMsg1389", "");
			CUSTOM_MSG_1390 = CustomMessageSettings.getString("CustomMsg1390", "");
			CUSTOM_MSG_1391 = CustomMessageSettings.getString("CustomMsg1391", "");
			CUSTOM_MSG_1392 = CustomMessageSettings.getString("CustomMsg1392", "");
			CUSTOM_MSG_1393 = CustomMessageSettings.getString("CustomMsg1393", "");
			CUSTOM_MSG_1394 = CustomMessageSettings.getString("CustomMsg1394", "");
			CUSTOM_MSG_1395 = CustomMessageSettings.getString("CustomMsg1395", "");
			CUSTOM_MSG_1396 = CustomMessageSettings.getString("CustomMsg1396", "");
			CUSTOM_MSG_1397 = CustomMessageSettings.getString("CustomMsg1397", "");
			CUSTOM_MSG_1398 = CustomMessageSettings.getString("CustomMsg1398", "");
			CUSTOM_MSG_1399 = CustomMessageSettings.getString("CustomMsg1399", "");
			CUSTOM_MSG_1400 = CustomMessageSettings.getString("CustomMsg1400", "");
			CUSTOM_MSG_1401 = CustomMessageSettings.getString("CustomMsg1401", "");
			CUSTOM_MSG_1402 = CustomMessageSettings.getString("CustomMsg1402", "");
			CUSTOM_MSG_1403 = CustomMessageSettings.getString("CustomMsg1403", "");
			CUSTOM_MSG_1404 = CustomMessageSettings.getString("CustomMsg1404", "");
			CUSTOM_MSG_1405 = CustomMessageSettings.getString("CustomMsg1405", "");
			CUSTOM_MSG_1406 = CustomMessageSettings.getString("CustomMsg1406", "");
			CUSTOM_MSG_1407 = CustomMessageSettings.getString("CustomMsg1407", "");
			CUSTOM_MSG_1408 = CustomMessageSettings.getString("CustomMsg1408", "");
			CUSTOM_MSG_1409 = CustomMessageSettings.getString("CustomMsg1409", "");
			CUSTOM_MSG_1410 = CustomMessageSettings.getString("CustomMsg1410", "");
			CUSTOM_MSG_1411 = CustomMessageSettings.getString("CustomMsg1411", "");
			CUSTOM_MSG_1412 = CustomMessageSettings.getString("CustomMsg1412", "");
			CUSTOM_MSG_1413 = CustomMessageSettings.getString("CustomMsg1413", "");
			CUSTOM_MSG_1414 = CustomMessageSettings.getString("CustomMsg1414", "");
			CUSTOM_MSG_1415 = CustomMessageSettings.getString("CustomMsg1415", "");
			CUSTOM_MSG_1416 = CustomMessageSettings.getString("CustomMsg1416", "");
			CUSTOM_MSG_1417 = CustomMessageSettings.getString("CustomMsg1417", "");
			CUSTOM_MSG_1418 = CustomMessageSettings.getString("CustomMsg1418", "");
			CUSTOM_MSG_1419 = CustomMessageSettings.getString("CustomMsg1419", "");
			CUSTOM_MSG_1420 = CustomMessageSettings.getString("CustomMsg1420", "");
			CUSTOM_MSG_1421 = CustomMessageSettings.getString("CustomMsg1421", "");
			CUSTOM_MSG_1422 = CustomMessageSettings.getString("CustomMsg1422", "");
			CUSTOM_MSG_1423 = CustomMessageSettings.getString("CustomMsg1423", "");
			CUSTOM_MSG_1424 = CustomMessageSettings.getString("CustomMsg1424", "");
			CUSTOM_MSG_1425 = CustomMessageSettings.getString("CustomMsg1425", "");
			CUSTOM_MSG_1426 = CustomMessageSettings.getString("CustomMsg1426", "");
			CUSTOM_MSG_1427 = CustomMessageSettings.getString("CustomMsg1427", "");
			CUSTOM_MSG_1428 = CustomMessageSettings.getString("CustomMsg1428", "");
			CUSTOM_MSG_1429 = CustomMessageSettings.getString("CustomMsg1429", "");
			CUSTOM_MSG_1430 = CustomMessageSettings.getString("CustomMsg1430", "");
			CUSTOM_MSG_1431 = CustomMessageSettings.getString("CustomMsg1431", "");
			CUSTOM_MSG_1432 = CustomMessageSettings.getString("CustomMsg1432", "");
			CUSTOM_MSG_1433 = CustomMessageSettings.getString("CustomMsg1433", "");
			CUSTOM_MSG_1434 = CustomMessageSettings.getString("CustomMsg1434", "");
			CUSTOM_MSG_1435 = CustomMessageSettings.getString("CustomMsg1435", "");
			CUSTOM_MSG_1436 = CustomMessageSettings.getString("CustomMsg1436", "");
			CUSTOM_MSG_1437 = CustomMessageSettings.getString("CustomMsg1437", "");
			CUSTOM_MSG_1438 = CustomMessageSettings.getString("CustomMsg1438", "");
			CUSTOM_MSG_1439 = CustomMessageSettings.getString("CustomMsg1439", "");
			CUSTOM_MSG_1440 = CustomMessageSettings.getString("CustomMsg1440", "");
			CUSTOM_MSG_1441 = CustomMessageSettings.getString("CustomMsg1441", "");
			CUSTOM_MSG_1442 = CustomMessageSettings.getString("CustomMsg1442", "");
			CUSTOM_MSG_1443 = CustomMessageSettings.getString("CustomMsg1443", "");
			CUSTOM_MSG_1444 = CustomMessageSettings.getString("CustomMsg1444", "");
			CUSTOM_MSG_1445 = CustomMessageSettings.getString("CustomMsg1445", "");
			CUSTOM_MSG_1446 = CustomMessageSettings.getString("CustomMsg1446", "");
			CUSTOM_MSG_1447 = CustomMessageSettings.getString("CustomMsg1447", "");
			CUSTOM_MSG_1448 = CustomMessageSettings.getString("CustomMsg1448", "");
			CUSTOM_MSG_1449 = CustomMessageSettings.getString("CustomMsg1449", "");
			CUSTOM_MSG_1450 = CustomMessageSettings.getString("CustomMsg1450", "");
			CUSTOM_MSG_1451 = CustomMessageSettings.getString("CustomMsg1451", "");
			CUSTOM_MSG_1452 = CustomMessageSettings.getString("CustomMsg1452", "");
			CUSTOM_MSG_1453 = CustomMessageSettings.getString("CustomMsg1453", "");
			CUSTOM_MSG_1454 = CustomMessageSettings.getString("CustomMsg1454", "");
			CUSTOM_MSG_1455 = CustomMessageSettings.getString("CustomMsg1455", "");
			CUSTOM_MSG_1456 = CustomMessageSettings.getString("CustomMsg1456", "");
			CUSTOM_MSG_1457 = CustomMessageSettings.getString("CustomMsg1457", "");
			CUSTOM_MSG_1458 = CustomMessageSettings.getString("CustomMsg1458", "");
			CUSTOM_MSG_1459 = CustomMessageSettings.getString("CustomMsg1459", "");
			CUSTOM_MSG_1460 = CustomMessageSettings.getString("CustomMsg1460", "");
			CUSTOM_MSG_1461 = CustomMessageSettings.getString("CustomMsg1461", "");
			CUSTOM_MSG_1462 = CustomMessageSettings.getString("CustomMsg1462", "");
			CUSTOM_MSG_1463 = CustomMessageSettings.getString("CustomMsg1463", "");
			CUSTOM_MSG_1464 = CustomMessageSettings.getString("CustomMsg1464", "");
			CUSTOM_MSG_1465 = CustomMessageSettings.getString("CustomMsg1465", "");
			CUSTOM_MSG_1466 = CustomMessageSettings.getString("CustomMsg1466", "");
			CUSTOM_MSG_1467 = CustomMessageSettings.getString("CustomMsg1467", "");
			CUSTOM_MSG_1468 = CustomMessageSettings.getString("CustomMsg1468", "");
			CUSTOM_MSG_1469 = CustomMessageSettings.getString("CustomMsg1469", "");
			CUSTOM_MSG_1470 = CustomMessageSettings.getString("CustomMsg1470", "");
			CUSTOM_MSG_1471 = CustomMessageSettings.getString("CustomMsg1471", "");
			CUSTOM_MSG_1472 = CustomMessageSettings.getString("CustomMsg1472", "");
			CUSTOM_MSG_1473 = CustomMessageSettings.getString("CustomMsg1473", "");
			CUSTOM_MSG_1474 = CustomMessageSettings.getString("CustomMsg1474", "");
			CUSTOM_MSG_1475 = CustomMessageSettings.getString("CustomMsg1475", "");
			CUSTOM_MSG_1476 = CustomMessageSettings.getString("CustomMsg1476", "");
			CUSTOM_MSG_1477 = CustomMessageSettings.getString("CustomMsg1477", "");
			CUSTOM_MSG_1478 = CustomMessageSettings.getString("CustomMsg1478", "");
			CUSTOM_MSG_1479 = CustomMessageSettings.getString("CustomMsg1479", "");
			CUSTOM_MSG_1480 = CustomMessageSettings.getString("CustomMsg1480", "");
			CUSTOM_MSG_1481 = CustomMessageSettings.getString("CustomMsg1481", "");
			CUSTOM_MSG_1482 = CustomMessageSettings.getString("CustomMsg1482", "");
			CUSTOM_MSG_1483 = CustomMessageSettings.getString("CustomMsg1483", "");
			CUSTOM_MSG_1484 = CustomMessageSettings.getString("CustomMsg1484", "");
			CUSTOM_MSG_1485 = CustomMessageSettings.getString("CustomMsg1485", "");
			CUSTOM_MSG_1486 = CustomMessageSettings.getString("CustomMsg1486", "");
			CUSTOM_MSG_1487 = CustomMessageSettings.getString("CustomMsg1487", "");
			CUSTOM_MSG_1488 = CustomMessageSettings.getString("CustomMsg1488", "");
			CUSTOM_MSG_1489 = CustomMessageSettings.getString("CustomMsg1489", "");
			CUSTOM_MSG_1490 = CustomMessageSettings.getString("CustomMsg1490", "");
			CUSTOM_MSG_1491 = CustomMessageSettings.getString("CustomMsg1491", "");
			CUSTOM_MSG_1492 = CustomMessageSettings.getString("CustomMsg1492", "");
			CUSTOM_MSG_1493 = CustomMessageSettings.getString("CustomMsg1493", "");
			CUSTOM_MSG_1494 = CustomMessageSettings.getString("CustomMsg1494", "");
			CUSTOM_MSG_1495 = CustomMessageSettings.getString("CustomMsg1495", "");
			CUSTOM_MSG_1496 = CustomMessageSettings.getString("CustomMsg1496", "");
			CUSTOM_MSG_1497 = CustomMessageSettings.getString("CustomMsg1497", "");
			CUSTOM_MSG_1498 = CustomMessageSettings.getString("CustomMsg1498", "");
			CUSTOM_MSG_1499 = CustomMessageSettings.getString("CustomMsg1499", "");
			CUSTOM_MSG_1500 = CustomMessageSettings.getString("CustomMsg1500", "");
			CUSTOM_MSG_1501 = CustomMessageSettings.getString("CustomMsg1501", "");
			CUSTOM_MSG_1502 = CustomMessageSettings.getString("CustomMsg1502", "");
			CUSTOM_MSG_1503 = CustomMessageSettings.getString("CustomMsg1503", "");
			CUSTOM_MSG_1504 = CustomMessageSettings.getString("CustomMsg1504", "");
			CUSTOM_MSG_1505 = CustomMessageSettings.getString("CustomMsg1505", "");
			CUSTOM_MSG_1506 = CustomMessageSettings.getString("CustomMsg1506", "");
			CUSTOM_MSG_1507 = CustomMessageSettings.getString("CustomMsg1507", "");
			CUSTOM_MSG_1508 = CustomMessageSettings.getString("CustomMsg1508", "");
			CUSTOM_MSG_1509 = CustomMessageSettings.getString("CustomMsg1509", "");
			CUSTOM_MSG_1510 = CustomMessageSettings.getString("CustomMsg1510", "");
			CUSTOM_MSG_1511 = CustomMessageSettings.getString("CustomMsg1511", "");
			CUSTOM_MSG_1512 = CustomMessageSettings.getString("CustomMsg1512", "");
			CUSTOM_MSG_1513 = CustomMessageSettings.getString("CustomMsg1513", "");
			CUSTOM_MSG_1514 = CustomMessageSettings.getString("CustomMsg1514", "");
			CUSTOM_MSG_1515 = CustomMessageSettings.getString("CustomMsg1515", "");
			CUSTOM_MSG_1516 = CustomMessageSettings.getString("CustomMsg1516", "");
			CUSTOM_MSG_1517 = CustomMessageSettings.getString("CustomMsg1517", "");
			CUSTOM_MSG_1518 = CustomMessageSettings.getString("CustomMsg1518", "");
			CUSTOM_MSG_1519 = CustomMessageSettings.getString("CustomMsg1519", "");
			CUSTOM_MSG_1520 = CustomMessageSettings.getString("CustomMsg1520", "");
			CUSTOM_MSG_1521 = CustomMessageSettings.getString("CustomMsg1521", "");
			CUSTOM_MSG_1522 = CustomMessageSettings.getString("CustomMsg1522", "");
			CUSTOM_MSG_1523 = CustomMessageSettings.getString("CustomMsg1523", "");
			CUSTOM_MSG_1524 = CustomMessageSettings.getString("CustomMsg1524", "");
			CUSTOM_MSG_1525 = CustomMessageSettings.getString("CustomMsg1525", "");
			CUSTOM_MSG_1526 = CustomMessageSettings.getString("CustomMsg1526", "");
			CUSTOM_MSG_1527 = CustomMessageSettings.getString("CustomMsg1527", "");
			CUSTOM_MSG_1528 = CustomMessageSettings.getString("CustomMsg1528", "");
			CUSTOM_MSG_1529 = CustomMessageSettings.getString("CustomMsg1529", "");
			CUSTOM_MSG_1530 = CustomMessageSettings.getString("CustomMsg1530", "");
			CUSTOM_MSG_1531 = CustomMessageSettings.getString("CustomMsg1531", "");
			CUSTOM_MSG_1532 = CustomMessageSettings.getString("CustomMsg1532", "");
			CUSTOM_MSG_1533 = CustomMessageSettings.getString("CustomMsg1533", "");
			CUSTOM_MSG_1534 = CustomMessageSettings.getString("CustomMsg1534", "");
			CUSTOM_MSG_1535 = CustomMessageSettings.getString("CustomMsg1535", "");
			CUSTOM_MSG_1536 = CustomMessageSettings.getString("CustomMsg1536", "");
			CUSTOM_MSG_1537 = CustomMessageSettings.getString("CustomMsg1537", "");
			CUSTOM_MSG_1538 = CustomMessageSettings.getString("CustomMsg1538", "");
			CUSTOM_MSG_1539 = CustomMessageSettings.getString("CustomMsg1539", "");
			CUSTOM_MSG_1540 = CustomMessageSettings.getString("CustomMsg1540", "");
			CUSTOM_MSG_1541 = CustomMessageSettings.getString("CustomMsg1541", "");
			CUSTOM_MSG_1542 = CustomMessageSettings.getString("CustomMsg1542", "");
			CUSTOM_MSG_1543 = CustomMessageSettings.getString("CustomMsg1543", "");
			CUSTOM_MSG_1544 = CustomMessageSettings.getString("CustomMsg1544", "");
			CUSTOM_MSG_1545 = CustomMessageSettings.getString("CustomMsg1545", "");
			CUSTOM_MSG_1546 = CustomMessageSettings.getString("CustomMsg1546", "");
			CUSTOM_MSG_1547 = CustomMessageSettings.getString("CustomMsg1547", "");
			CUSTOM_MSG_1548 = CustomMessageSettings.getString("CustomMsg1548", "");
			CUSTOM_MSG_1549 = CustomMessageSettings.getString("CustomMsg1549", "");
			CUSTOM_MSG_1550 = CustomMessageSettings.getString("CustomMsg1550", "");
			CUSTOM_MSG_1551 = CustomMessageSettings.getString("CustomMsg1551", "");
			CUSTOM_MSG_1552 = CustomMessageSettings.getString("CustomMsg1552", "");
			CUSTOM_MSG_1553 = CustomMessageSettings.getString("CustomMsg1553", "");
			CUSTOM_MSG_1554 = CustomMessageSettings.getString("CustomMsg1554", "");
			CUSTOM_MSG_1555 = CustomMessageSettings.getString("CustomMsg1555", "");
			CUSTOM_MSG_1556 = CustomMessageSettings.getString("CustomMsg1556", "");
			CUSTOM_MSG_1557 = CustomMessageSettings.getString("CustomMsg1557", "");
			CUSTOM_MSG_1558 = CustomMessageSettings.getString("CustomMsg1558", "");
			CUSTOM_MSG_1559 = CustomMessageSettings.getString("CustomMsg1559", "");
			CUSTOM_MSG_1560 = CustomMessageSettings.getString("CustomMsg1560", "");
			CUSTOM_MSG_1561 = CustomMessageSettings.getString("CustomMsg1561", "");
			CUSTOM_MSG_1562 = CustomMessageSettings.getString("CustomMsg1562", "");
			CUSTOM_MSG_1563 = CustomMessageSettings.getString("CustomMsg1563", "");
			CUSTOM_MSG_1564 = CustomMessageSettings.getString("CustomMsg1564", "");
			CUSTOM_MSG_1565 = CustomMessageSettings.getString("CustomMsg1565", "");
			CUSTOM_MSG_1566 = CustomMessageSettings.getString("CustomMsg1566", "");
			CUSTOM_MSG_1567 = CustomMessageSettings.getString("CustomMsg1567", "");
			CUSTOM_MSG_1568 = CustomMessageSettings.getString("CustomMsg1568", "");
			CUSTOM_MSG_1569 = CustomMessageSettings.getString("CustomMsg1569", "");
			CUSTOM_MSG_1570 = CustomMessageSettings.getString("CustomMsg1570", "");
			CUSTOM_MSG_1571 = CustomMessageSettings.getString("CustomMsg1571", "");
			CUSTOM_MSG_1572 = CustomMessageSettings.getString("CustomMsg1572", "");
			CUSTOM_MSG_1573 = CustomMessageSettings.getString("CustomMsg1573", "");
			CUSTOM_MSG_1574 = CustomMessageSettings.getString("CustomMsg1574", "");
			CUSTOM_MSG_1575 = CustomMessageSettings.getString("CustomMsg1575", "");
			CUSTOM_MSG_1576 = CustomMessageSettings.getString("CustomMsg1576", "");
			CUSTOM_MSG_1577 = CustomMessageSettings.getString("CustomMsg1577", "");
			CUSTOM_MSG_1578 = CustomMessageSettings.getString("CustomMsg1578", "");
			CUSTOM_MSG_1579 = CustomMessageSettings.getString("CustomMsg1579", "");
			CUSTOM_MSG_1580 = CustomMessageSettings.getString("CustomMsg1580", "");
			CUSTOM_MSG_1581 = CustomMessageSettings.getString("CustomMsg1581", "");
			CUSTOM_MSG_1582 = CustomMessageSettings.getString("CustomMsg1582", "");
			CUSTOM_MSG_1583 = CustomMessageSettings.getString("CustomMsg1583", "");
			CUSTOM_MSG_1584 = CustomMessageSettings.getString("CustomMsg1584", "");
			CUSTOM_MSG_1585 = CustomMessageSettings.getString("CustomMsg1585", "");
			CUSTOM_MSG_1586 = CustomMessageSettings.getString("CustomMsg1586", "");
			CUSTOM_MSG_1587 = CustomMessageSettings.getString("CustomMsg1587", "");
			CUSTOM_MSG_1588 = CustomMessageSettings.getString("CustomMsg1588", "");
			CUSTOM_MSG_1589 = CustomMessageSettings.getString("CustomMsg1589", "");
			CUSTOM_MSG_1590 = CustomMessageSettings.getString("CustomMsg1590", "");
			CUSTOM_MSG_1591 = CustomMessageSettings.getString("CustomMsg1591", "");
			CUSTOM_MSG_1592 = CustomMessageSettings.getString("CustomMsg1592", "");
			CUSTOM_MSG_1593 = CustomMessageSettings.getString("CustomMsg1593", "");
			CUSTOM_MSG_1594 = CustomMessageSettings.getString("CustomMsg1594", "");
			CUSTOM_MSG_1595 = CustomMessageSettings.getString("CustomMsg1595", "");
			CUSTOM_MSG_1596 = CustomMessageSettings.getString("CustomMsg1596", "");
			CUSTOM_MSG_1597 = CustomMessageSettings.getString("CustomMsg1597", "");
			CUSTOM_MSG_1598 = CustomMessageSettings.getString("CustomMsg1598", "");
			CUSTOM_MSG_1599 = CustomMessageSettings.getString("CustomMsg1599", "");
			CUSTOM_MSG_1600 = CustomMessageSettings.getString("CustomMsg1600", "");
			CUSTOM_MSG_1601 = CustomMessageSettings.getString("CustomMsg1601", "");
			CUSTOM_MSG_1602 = CustomMessageSettings.getString("CustomMsg1602", "");
			CUSTOM_MSG_1603 = CustomMessageSettings.getString("CustomMsg1603", "");
			CUSTOM_MSG_1604 = CustomMessageSettings.getString("CustomMsg1604", "");
			CUSTOM_MSG_1605 = CustomMessageSettings.getString("CustomMsg1605", "");
			CUSTOM_MSG_1606 = CustomMessageSettings.getString("CustomMsg1606", "");
			CUSTOM_MSG_1607 = CustomMessageSettings.getString("CustomMsg1607", "");
			CUSTOM_MSG_1608 = CustomMessageSettings.getString("CustomMsg1608", "");
			CUSTOM_MSG_1609 = CustomMessageSettings.getString("CustomMsg1609", "");
			CUSTOM_MSG_1610 = CustomMessageSettings.getString("CustomMsg1610", "");
			CUSTOM_MSG_1611 = CustomMessageSettings.getString("CustomMsg1611", "");
			CUSTOM_MSG_1612 = CustomMessageSettings.getString("CustomMsg1612", "");
			CUSTOM_MSG_1613 = CustomMessageSettings.getString("CustomMsg1613", "");
			CUSTOM_MSG_1614 = CustomMessageSettings.getString("CustomMsg1614", "");
			CUSTOM_MSG_1615 = CustomMessageSettings.getString("CustomMsg1615", "");
			CUSTOM_MSG_1616 = CustomMessageSettings.getString("CustomMsg1616", "");
			CUSTOM_MSG_1617 = CustomMessageSettings.getString("CustomMsg1617", "");
			CUSTOM_MSG_1618 = CustomMessageSettings.getString("CustomMsg1618", "");
			CUSTOM_MSG_1619 = CustomMessageSettings.getString("CustomMsg1619", "");
			CUSTOM_MSG_1620 = CustomMessageSettings.getString("CustomMsg1620", "");
			CUSTOM_MSG_1621 = CustomMessageSettings.getString("CustomMsg1621", "");
			CUSTOM_MSG_1622 = CustomMessageSettings.getString("CustomMsg1622", "");
			CUSTOM_MSG_1623 = CustomMessageSettings.getString("CustomMsg1623", "");
			CUSTOM_MSG_1624 = CustomMessageSettings.getString("CustomMsg1624", "");
			CUSTOM_MSG_1625 = CustomMessageSettings.getString("CustomMsg1625", "");
			CUSTOM_MSG_1626 = CustomMessageSettings.getString("CustomMsg1626", "");
			CUSTOM_MSG_1627 = CustomMessageSettings.getString("CustomMsg1627", "");
			CUSTOM_MSG_1628 = CustomMessageSettings.getString("CustomMsg1628", "");
			CUSTOM_MSG_1629 = CustomMessageSettings.getString("CustomMsg1629", "");
			CUSTOM_MSG_1630 = CustomMessageSettings.getString("CustomMsg1630", "");
			CUSTOM_MSG_1631 = CustomMessageSettings.getString("CustomMsg1631", "");
			CUSTOM_MSG_1632 = CustomMessageSettings.getString("CustomMsg1632", "");
			CUSTOM_MSG_1633 = CustomMessageSettings.getString("CustomMsg1633", "");
			CUSTOM_MSG_1634 = CustomMessageSettings.getString("CustomMsg1634", "");
			CUSTOM_MSG_1635 = CustomMessageSettings.getString("CustomMsg1635", "");
			CUSTOM_MSG_1636 = CustomMessageSettings.getString("CustomMsg1636", "");
			CUSTOM_MSG_1637 = CustomMessageSettings.getString("CustomMsg1637", "");
			CUSTOM_MSG_1638 = CustomMessageSettings.getString("CustomMsg1638", "");
			CUSTOM_MSG_1639 = CustomMessageSettings.getString("CustomMsg1639", "");
			CUSTOM_MSG_1640 = CustomMessageSettings.getString("CustomMsg1640", "");
			CUSTOM_MSG_1641 = CustomMessageSettings.getString("CustomMsg1641", "");
			CUSTOM_MSG_1642 = CustomMessageSettings.getString("CustomMsg1642", "");
			CUSTOM_MSG_1643 = CustomMessageSettings.getString("CustomMsg1643", "");
			CUSTOM_MSG_1644 = CustomMessageSettings.getString("CustomMsg1644", "");
			CUSTOM_MSG_1645 = CustomMessageSettings.getString("CustomMsg1645", "");
			CUSTOM_MSG_1646 = CustomMessageSettings.getString("CustomMsg1646", "");
			CUSTOM_MSG_1647 = CustomMessageSettings.getString("CustomMsg1647", "");
			CUSTOM_MSG_1648 = CustomMessageSettings.getString("CustomMsg1648", "");
			CUSTOM_MSG_1649 = CustomMessageSettings.getString("CustomMsg1649", "");
			CUSTOM_MSG_1650 = CustomMessageSettings.getString("CustomMsg1650", "");
			CUSTOM_MSG_1651 = CustomMessageSettings.getString("CustomMsg1651", "");
			CUSTOM_MSG_1652 = CustomMessageSettings.getString("CustomMsg1652", "");
			CUSTOM_MSG_1653 = CustomMessageSettings.getString("CustomMsg1653", "");
			CUSTOM_MSG_1654 = CustomMessageSettings.getString("CustomMsg1654", "");
			CUSTOM_MSG_1655 = CustomMessageSettings.getString("CustomMsg1655", "");
			CUSTOM_MSG_1656 = CustomMessageSettings.getString("CustomMsg1656", "");
			CUSTOM_MSG_1657 = CustomMessageSettings.getString("CustomMsg1657", "");
			CUSTOM_MSG_1658 = CustomMessageSettings.getString("CustomMsg1658", "");
			CUSTOM_MSG_1659 = CustomMessageSettings.getString("CustomMsg1659", "");
			CUSTOM_MSG_1660 = CustomMessageSettings.getString("CustomMsg1660", "");
			CUSTOM_MSG_1661 = CustomMessageSettings.getString("CustomMsg1661", "");
			CUSTOM_MSG_1662 = CustomMessageSettings.getString("CustomMsg1662", "");
			CUSTOM_MSG_1663 = CustomMessageSettings.getString("CustomMsg1663", "");
			CUSTOM_MSG_1664 = CustomMessageSettings.getString("CustomMsg1664", "");
			CUSTOM_MSG_1665 = CustomMessageSettings.getString("CustomMsg1665", "");
			CUSTOM_MSG_1666 = CustomMessageSettings.getString("CustomMsg1666", "");
			CUSTOM_MSG_1667 = CustomMessageSettings.getString("CustomMsg1667", "");
			CUSTOM_MSG_1668 = CustomMessageSettings.getString("CustomMsg1668", "");
			CUSTOM_MSG_1669 = CustomMessageSettings.getString("CustomMsg1669", "");
			CUSTOM_MSG_1670 = CustomMessageSettings.getString("CustomMsg1670", "");
			CUSTOM_MSG_1671 = CustomMessageSettings.getString("CustomMsg1671", "");
			CUSTOM_MSG_1672 = CustomMessageSettings.getString("CustomMsg1672", "");
			CUSTOM_MSG_1673 = CustomMessageSettings.getString("CustomMsg1673", "");
			CUSTOM_MSG_1674 = CustomMessageSettings.getString("CustomMsg1674", "");
			CUSTOM_MSG_1675 = CustomMessageSettings.getString("CustomMsg1675", "");
			CUSTOM_MSG_1676 = CustomMessageSettings.getString("CustomMsg1676", "");
			CUSTOM_MSG_1677 = CustomMessageSettings.getString("CustomMsg1677", "");
			CUSTOM_MSG_1678 = CustomMessageSettings.getString("CustomMsg1678", "");
			CUSTOM_MSG_1679 = CustomMessageSettings.getString("CustomMsg1679", "");
			CUSTOM_MSG_1680 = CustomMessageSettings.getString("CustomMsg1680", "");
			CUSTOM_MSG_1681 = CustomMessageSettings.getString("CustomMsg1681", "");
			CUSTOM_MSG_1682 = CustomMessageSettings.getString("CustomMsg1682", "");
			CUSTOM_MSG_1683 = CustomMessageSettings.getString("CustomMsg1683", "");
			CUSTOM_MSG_1684 = CustomMessageSettings.getString("CustomMsg1684", "");
			CUSTOM_MSG_1685 = CustomMessageSettings.getString("CustomMsg1685", "");
			CUSTOM_MSG_1686 = CustomMessageSettings.getString("CustomMsg1686", "");
			CUSTOM_MSG_1687 = CustomMessageSettings.getString("CustomMsg1687", "");
			CUSTOM_MSG_1688 = CustomMessageSettings.getString("CustomMsg1688", "");
			CUSTOM_MSG_1689 = CustomMessageSettings.getString("CustomMsg1689", "");
			CUSTOM_MSG_1690 = CustomMessageSettings.getString("CustomMsg1690", "");
			CUSTOM_MSG_1691 = CustomMessageSettings.getString("CustomMsg1691", "");
			CUSTOM_MSG_1692 = CustomMessageSettings.getString("CustomMsg1692", "");
			CUSTOM_MSG_1693 = CustomMessageSettings.getString("CustomMsg1693", "");
			CUSTOM_MSG_1694 = CustomMessageSettings.getString("CustomMsg1694", "");
			CUSTOM_MSG_1695 = CustomMessageSettings.getString("CustomMsg1695", "");
			CUSTOM_MSG_1696 = CustomMessageSettings.getString("CustomMsg1696", "");
			CUSTOM_MSG_1697 = CustomMessageSettings.getString("CustomMsg1697", "");
			CUSTOM_MSG_1698 = CustomMessageSettings.getString("CustomMsg1698", "");
			CUSTOM_MSG_1699 = CustomMessageSettings.getString("CustomMsg1699", "");
			CUSTOM_MSG_1700 = CustomMessageSettings.getString("CustomMsg1700", "");
			CUSTOM_MSG_1701 = CustomMessageSettings.getString("CustomMsg1701", "");
			CUSTOM_MSG_1702 = CustomMessageSettings.getString("CustomMsg1702", "");
			CUSTOM_MSG_1703 = CustomMessageSettings.getString("CustomMsg1703", "");
			CUSTOM_MSG_1704 = CustomMessageSettings.getString("CustomMsg1704", "");
			CUSTOM_MSG_1705 = CustomMessageSettings.getString("CustomMsg1705", "");
			CUSTOM_MSG_1706 = CustomMessageSettings.getString("CustomMsg1706", "");
			CUSTOM_MSG_1707 = CustomMessageSettings.getString("CustomMsg1707", "");
			CUSTOM_MSG_1708 = CustomMessageSettings.getString("CustomMsg1708", "");
			CUSTOM_MSG_1709 = CustomMessageSettings.getString("CustomMsg1709", "");
			CUSTOM_MSG_1710 = CustomMessageSettings.getString("CustomMsg1710", "");
			CUSTOM_MSG_1711 = CustomMessageSettings.getString("CustomMsg1711", "");
			CUSTOM_MSG_1712 = CustomMessageSettings.getString("CustomMsg1712", "");
			CUSTOM_MSG_1713 = CustomMessageSettings.getString("CustomMsg1713", "");
			CUSTOM_MSG_1714 = CustomMessageSettings.getString("CustomMsg1714", "");
			CUSTOM_MSG_1715 = CustomMessageSettings.getString("CustomMsg1715", "");
			CUSTOM_MSG_1716 = CustomMessageSettings.getString("CustomMsg1716", "");
			CUSTOM_MSG_1717 = CustomMessageSettings.getString("CustomMsg1717", "");
			CUSTOM_MSG_1718 = CustomMessageSettings.getString("CustomMsg1718", "");
			CUSTOM_MSG_1719 = CustomMessageSettings.getString("CustomMsg1719", "");
			CUSTOM_MSG_1720 = CustomMessageSettings.getString("CustomMsg1720", "");
			CUSTOM_MSG_1721 = CustomMessageSettings.getString("CustomMsg1721", "");
			CUSTOM_MSG_1722 = CustomMessageSettings.getString("CustomMsg1722", "");
			CUSTOM_MSG_1723 = CustomMessageSettings.getString("CustomMsg1723", "");
			CUSTOM_MSG_1724 = CustomMessageSettings.getString("CustomMsg1724", "");
			CUSTOM_MSG_1725 = CustomMessageSettings.getString("CustomMsg1725", "");
			CUSTOM_MSG_1726 = CustomMessageSettings.getString("CustomMsg1726", "");
			CUSTOM_MSG_1727 = CustomMessageSettings.getString("CustomMsg1727", "");
			CUSTOM_MSG_1728 = CustomMessageSettings.getString("CustomMsg1728", "");
			CUSTOM_MSG_1729 = CustomMessageSettings.getString("CustomMsg1729", "");
			CUSTOM_MSG_1730 = CustomMessageSettings.getString("CustomMsg1730", "");
			CUSTOM_MSG_1731 = CustomMessageSettings.getString("CustomMsg1731", "");
			CUSTOM_MSG_1732 = CustomMessageSettings.getString("CustomMsg1732", "");
			CUSTOM_MSG_1733 = CustomMessageSettings.getString("CustomMsg1733", "");
			CUSTOM_MSG_1734 = CustomMessageSettings.getString("CustomMsg1734", "");
			CUSTOM_MSG_1735 = CustomMessageSettings.getString("CustomMsg1735", "");
			CUSTOM_MSG_1736 = CustomMessageSettings.getString("CustomMsg1736", "");
			CUSTOM_MSG_1737 = CustomMessageSettings.getString("CustomMsg1737", "");
			CUSTOM_MSG_1738 = CustomMessageSettings.getString("CustomMsg1738", "");
			CUSTOM_MSG_1739 = CustomMessageSettings.getString("CustomMsg1739", "");
			CUSTOM_MSG_1740 = CustomMessageSettings.getString("CustomMsg1740", "");
			CUSTOM_MSG_1741 = CustomMessageSettings.getString("CustomMsg1741", "");
			CUSTOM_MSG_1742 = CustomMessageSettings.getString("CustomMsg1742", "");
			CUSTOM_MSG_1743 = CustomMessageSettings.getString("CustomMsg1743", "");
			CUSTOM_MSG_1744 = CustomMessageSettings.getString("CustomMsg1744", "");
			CUSTOM_MSG_1745 = CustomMessageSettings.getString("CustomMsg1745", "");
			CUSTOM_MSG_1746 = CustomMessageSettings.getString("CustomMsg1746", "");
			CUSTOM_MSG_1747 = CustomMessageSettings.getString("CustomMsg1747", "");
			CUSTOM_MSG_1748 = CustomMessageSettings.getString("CustomMsg1748", "");
			CUSTOM_MSG_1749 = CustomMessageSettings.getString("CustomMsg1749", "");
			CUSTOM_MSG_1750 = CustomMessageSettings.getString("CustomMsg1750", "");
			CUSTOM_MSG_1751 = CustomMessageSettings.getString("CustomMsg1751", "");
			CUSTOM_MSG_1752 = CustomMessageSettings.getString("CustomMsg1752", "");
			CUSTOM_MSG_1753 = CustomMessageSettings.getString("CustomMsg1753", "");
			CUSTOM_MSG_1754 = CustomMessageSettings.getString("CustomMsg1754", "");
			CUSTOM_MSG_1755 = CustomMessageSettings.getString("CustomMsg1755", "");
			CUSTOM_MSG_1756 = CustomMessageSettings.getString("CustomMsg1756", "");
			CUSTOM_MSG_1757 = CustomMessageSettings.getString("CustomMsg1757", "");
			CUSTOM_MSG_1758 = CustomMessageSettings.getString("CustomMsg1758", "");
			CUSTOM_MSG_1759 = CustomMessageSettings.getString("CustomMsg1759", "");
			CUSTOM_MSG_1760 = CustomMessageSettings.getString("CustomMsg1760", "");
			CUSTOM_MSG_1761 = CustomMessageSettings.getString("CustomMsg1761", "");
			CUSTOM_MSG_1762 = CustomMessageSettings.getString("CustomMsg1762", "");
			CUSTOM_MSG_1763 = CustomMessageSettings.getString("CustomMsg1763", "");
			CUSTOM_MSG_1764 = CustomMessageSettings.getString("CustomMsg1764", "");
			CUSTOM_MSG_1765 = CustomMessageSettings.getString("CustomMsg1765", "");
			CUSTOM_MSG_1766 = CustomMessageSettings.getString("CustomMsg1766", "");
			CUSTOM_MSG_1767 = CustomMessageSettings.getString("CustomMsg1767", "");
			CUSTOM_MSG_1768 = CustomMessageSettings.getString("CustomMsg1768", "");
			CUSTOM_MSG_1769 = CustomMessageSettings.getString("CustomMsg1769", "");
			CUSTOM_MSG_1770 = CustomMessageSettings.getString("CustomMsg1770", "");
			CUSTOM_MSG_1771 = CustomMessageSettings.getString("CustomMsg1771", "");
			CUSTOM_MSG_1772 = CustomMessageSettings.getString("CustomMsg1772", "");
			CUSTOM_MSG_1773 = CustomMessageSettings.getString("CustomMsg1773", "");
			CUSTOM_MSG_1774 = CustomMessageSettings.getString("CustomMsg1774", "");
			CUSTOM_MSG_1775 = CustomMessageSettings.getString("CustomMsg1775", "");
			CUSTOM_MSG_1776 = CustomMessageSettings.getString("CustomMsg1776", "");
			CUSTOM_MSG_1777 = CustomMessageSettings.getString("CustomMsg1777", "");
			CUSTOM_MSG_1778 = CustomMessageSettings.getString("CustomMsg1778", "");
			CUSTOM_MSG_1779 = CustomMessageSettings.getString("CustomMsg1779", "");
			CUSTOM_MSG_1780 = CustomMessageSettings.getString("CustomMsg1780", "");
			CUSTOM_MSG_1781 = CustomMessageSettings.getString("CustomMsg1781", "");
			CUSTOM_MSG_1782 = CustomMessageSettings.getString("CustomMsg1782", "");
			CUSTOM_MSG_1783 = CustomMessageSettings.getString("CustomMsg1783", "");
			CUSTOM_MSG_1784 = CustomMessageSettings.getString("CustomMsg1784", "");
			CUSTOM_MSG_1785 = CustomMessageSettings.getString("CustomMsg1785", "");
			CUSTOM_MSG_1786 = CustomMessageSettings.getString("CustomMsg1786", "");
			CUSTOM_MSG_1787 = CustomMessageSettings.getString("CustomMsg1787", "");
			CUSTOM_MSG_1788 = CustomMessageSettings.getString("CustomMsg1788", "");
			CUSTOM_MSG_1789 = CustomMessageSettings.getString("CustomMsg1789", "");
			CUSTOM_MSG_1790 = CustomMessageSettings.getString("CustomMsg1790", "");
			CUSTOM_MSG_1791 = CustomMessageSettings.getString("CustomMsg1791", "");
			CUSTOM_MSG_1792 = CustomMessageSettings.getString("CustomMsg1792", "");
			CUSTOM_MSG_1793 = CustomMessageSettings.getString("CustomMsg1793", "");
			CUSTOM_MSG_1794 = CustomMessageSettings.getString("CustomMsg1794", "");
			CUSTOM_MSG_1795 = CustomMessageSettings.getString("CustomMsg1795", "");
			CUSTOM_MSG_1796 = CustomMessageSettings.getString("CustomMsg1796", "");
			CUSTOM_MSG_1797 = CustomMessageSettings.getString("CustomMsg1797", "");
			CUSTOM_MSG_1798 = CustomMessageSettings.getString("CustomMsg1798", "");
			CUSTOM_MSG_1799 = CustomMessageSettings.getString("CustomMsg1799", "");
			CUSTOM_MSG_1800 = CustomMessageSettings.getString("CustomMsg1800", "");
			CUSTOM_MSG_1801 = CustomMessageSettings.getString("CustomMsg1801", "");
			CUSTOM_MSG_1802 = CustomMessageSettings.getString("CustomMsg1802", "");
			CUSTOM_MSG_1803 = CustomMessageSettings.getString("CustomMsg1803", "");
			CUSTOM_MSG_1804 = CustomMessageSettings.getString("CustomMsg1804", "");
			CUSTOM_MSG_1805 = CustomMessageSettings.getString("CustomMsg1805", "");
			CUSTOM_MSG_1806 = CustomMessageSettings.getString("CustomMsg1806", "");
			CUSTOM_MSG_1807 = CustomMessageSettings.getString("CustomMsg1807", "");
			CUSTOM_MSG_1808 = CustomMessageSettings.getString("CustomMsg1808", "");
			CUSTOM_MSG_1809 = CustomMessageSettings.getString("CustomMsg1809", "");
			CUSTOM_MSG_1810 = CustomMessageSettings.getString("CustomMsg1810", "");
			CUSTOM_MSG_1811 = CustomMessageSettings.getString("CustomMsg1811", "");
			CUSTOM_MSG_1812 = CustomMessageSettings.getString("CustomMsg1812", "");
			CUSTOM_MSG_1813 = CustomMessageSettings.getString("CustomMsg1813", "");
			CUSTOM_MSG_1814 = CustomMessageSettings.getString("CustomMsg1814", "");
			CUSTOM_MSG_1815 = CustomMessageSettings.getString("CustomMsg1815", "");
			CUSTOM_MSG_1816 = CustomMessageSettings.getString("CustomMsg1816", "");
			CUSTOM_MSG_1817 = CustomMessageSettings.getString("CustomMsg1817", "");
			CUSTOM_MSG_1818 = CustomMessageSettings.getString("CustomMsg1818", "");
			CUSTOM_MSG_1819 = CustomMessageSettings.getString("CustomMsg1819", "");
			CUSTOM_MSG_1820 = CustomMessageSettings.getString("CustomMsg1820", "");
			CUSTOM_MSG_1821 = CustomMessageSettings.getString("CustomMsg1821", "");
			CUSTOM_MSG_1822 = CustomMessageSettings.getString("CustomMsg1822", "");
			CUSTOM_MSG_1823 = CustomMessageSettings.getString("CustomMsg1823", "");
			CUSTOM_MSG_1824 = CustomMessageSettings.getString("CustomMsg1824", "");
			CUSTOM_MSG_1825 = CustomMessageSettings.getString("CustomMsg1825", "");
			CUSTOM_MSG_1826 = CustomMessageSettings.getString("CustomMsg1826", "");
			CUSTOM_MSG_1827 = CustomMessageSettings.getString("CustomMsg1827", "");
			CUSTOM_MSG_1828 = CustomMessageSettings.getString("CustomMsg1828", "");
			CUSTOM_MSG_1829 = CustomMessageSettings.getString("CustomMsg1829", "");
			CUSTOM_MSG_1830 = CustomMessageSettings.getString("CustomMsg1830", "");
			CUSTOM_MSG_1831 = CustomMessageSettings.getString("CustomMsg1831", "");
			CUSTOM_MSG_1832 = CustomMessageSettings.getString("CustomMsg1832", "");
			CUSTOM_MSG_1833 = CustomMessageSettings.getString("CustomMsg1833", "");
			CUSTOM_MSG_1834 = CustomMessageSettings.getString("CustomMsg1834", "");
			CUSTOM_MSG_1835 = CustomMessageSettings.getString("CustomMsg1835", "");
			CUSTOM_MSG_1836 = CustomMessageSettings.getString("CustomMsg1836", "");
			CUSTOM_MSG_1837 = CustomMessageSettings.getString("CustomMsg1837", "");
			CUSTOM_MSG_1838 = CustomMessageSettings.getString("CustomMsg1838", "");
			CUSTOM_MSG_1839 = CustomMessageSettings.getString("CustomMsg1839", "");
			CUSTOM_MSG_1840 = CustomMessageSettings.getString("CustomMsg1840", "");
			CUSTOM_MSG_1841 = CustomMessageSettings.getString("CustomMsg1841", "");
			CUSTOM_MSG_1842 = CustomMessageSettings.getString("CustomMsg1842", "");
			CUSTOM_MSG_1843 = CustomMessageSettings.getString("CustomMsg1843", "");
			CUSTOM_MSG_1844 = CustomMessageSettings.getString("CustomMsg1844", "");
			CUSTOM_MSG_1845 = CustomMessageSettings.getString("CustomMsg1845", "");
			CUSTOM_MSG_1846 = CustomMessageSettings.getString("CustomMsg1846", "");
			CUSTOM_MSG_1847 = CustomMessageSettings.getString("CustomMsg1847", "");
			CUSTOM_MSG_1848 = CustomMessageSettings.getString("CustomMsg1848", "");
			CUSTOM_MSG_1849 = CustomMessageSettings.getString("CustomMsg1849", "");
			CUSTOM_MSG_1850 = CustomMessageSettings.getString("CustomMsg1850", "");
			CUSTOM_MSG_1851 = CustomMessageSettings.getString("CustomMsg1851", "");
			CUSTOM_MSG_1852 = CustomMessageSettings.getString("CustomMsg1852", "");
			CUSTOM_MSG_1853 = CustomMessageSettings.getString("CustomMsg1853", "");
			CUSTOM_MSG_1854 = CustomMessageSettings.getString("CustomMsg1854", "");
			CUSTOM_MSG_1855 = CustomMessageSettings.getString("CustomMsg1855", "");
			CUSTOM_MSG_1856 = CustomMessageSettings.getString("CustomMsg1856", "");
			CUSTOM_MSG_1857 = CustomMessageSettings.getString("CustomMsg1857", "");
			CUSTOM_MSG_1858 = CustomMessageSettings.getString("CustomMsg1858", "");
			CUSTOM_MSG_1859 = CustomMessageSettings.getString("CustomMsg1859", "");
			CUSTOM_MSG_1860 = CustomMessageSettings.getString("CustomMsg1860", "");
			CUSTOM_MSG_1861 = CustomMessageSettings.getString("CustomMsg1861", "");
			CUSTOM_MSG_1862 = CustomMessageSettings.getString("CustomMsg1862", "");
			CUSTOM_MSG_1863 = CustomMessageSettings.getString("CustomMsg1863", "");
			CUSTOM_MSG_1864 = CustomMessageSettings.getString("CustomMsg1864", "");
			CUSTOM_MSG_1865 = CustomMessageSettings.getString("CustomMsg1865", "");
			CUSTOM_MSG_1866 = CustomMessageSettings.getString("CustomMsg1866", "");
			CUSTOM_MSG_1867 = CustomMessageSettings.getString("CustomMsg1867", "");
			CUSTOM_MSG_1868 = CustomMessageSettings.getString("CustomMsg1868", "");
			CUSTOM_MSG_1869 = CustomMessageSettings.getString("CustomMsg1869", "");
			CUSTOM_MSG_1870 = CustomMessageSettings.getString("CustomMsg1870", "");
			CUSTOM_MSG_1871 = CustomMessageSettings.getString("CustomMsg1871", "");
			CUSTOM_MSG_1872 = CustomMessageSettings.getString("CustomMsg1872", "");
			CUSTOM_MSG_1873 = CustomMessageSettings.getString("CustomMsg1873", "");
			CUSTOM_MSG_1874 = CustomMessageSettings.getString("CustomMsg1874", "");
			CUSTOM_MSG_1875 = CustomMessageSettings.getString("CustomMsg1875", "");
			CUSTOM_MSG_1876 = CustomMessageSettings.getString("CustomMsg1876", "");
			CUSTOM_MSG_1877 = CustomMessageSettings.getString("CustomMsg1877", "");
			CUSTOM_MSG_1878 = CustomMessageSettings.getString("CustomMsg1878", "");
			CUSTOM_MSG_1879 = CustomMessageSettings.getString("CustomMsg1879", "");
			CUSTOM_MSG_1880 = CustomMessageSettings.getString("CustomMsg1880", "");
			CUSTOM_MSG_1881 = CustomMessageSettings.getString("CustomMsg1881", "");
			CUSTOM_MSG_1882 = CustomMessageSettings.getString("CustomMsg1882", "");
			CUSTOM_MSG_1883 = CustomMessageSettings.getString("CustomMsg1883", "");
			CUSTOM_MSG_1884 = CustomMessageSettings.getString("CustomMsg1884", "");
			CUSTOM_MSG_1885 = CustomMessageSettings.getString("CustomMsg1885", "");
			CUSTOM_MSG_1886 = CustomMessageSettings.getString("CustomMsg1886", "");
			CUSTOM_MSG_1887 = CustomMessageSettings.getString("CustomMsg1887", "");
			CUSTOM_MSG_1888 = CustomMessageSettings.getString("CustomMsg1888", "");
			CUSTOM_MSG_1889 = CustomMessageSettings.getString("CustomMsg1889", "");
			CUSTOM_MSG_1890 = CustomMessageSettings.getString("CustomMsg1890", "");
			CUSTOM_MSG_1891 = CustomMessageSettings.getString("CustomMsg1891", "");
			CUSTOM_MSG_1892 = CustomMessageSettings.getString("CustomMsg1892", "");
			CUSTOM_MSG_1893 = CustomMessageSettings.getString("CustomMsg1893", "");
			CUSTOM_MSG_1894 = CustomMessageSettings.getString("CustomMsg1894", "");
			CUSTOM_MSG_1895 = CustomMessageSettings.getString("CustomMsg1895", "");
			CUSTOM_MSG_1896 = CustomMessageSettings.getString("CustomMsg1896", "");
			CUSTOM_MSG_1897 = CustomMessageSettings.getString("CustomMsg1897", "");
			CUSTOM_MSG_1898 = CustomMessageSettings.getString("CustomMsg1898", "");
			CUSTOM_MSG_1899 = CustomMessageSettings.getString("CustomMsg1899", "");
			CUSTOM_MSG_1900 = CustomMessageSettings.getString("CustomMsg1900", "");
			CUSTOM_MSG_1901 = CustomMessageSettings.getString("CustomMsg1901", "");
			CUSTOM_MSG_1902 = CustomMessageSettings.getString("CustomMsg1902", "");
			CUSTOM_MSG_1903 = CustomMessageSettings.getString("CustomMsg1903", "");
			CUSTOM_MSG_1904 = CustomMessageSettings.getString("CustomMsg1904", "");
			CUSTOM_MSG_1905 = CustomMessageSettings.getString("CustomMsg1905", "");
			CUSTOM_MSG_1906 = CustomMessageSettings.getString("CustomMsg1906", "");
			CUSTOM_MSG_1907 = CustomMessageSettings.getString("CustomMsg1907", "");
			CUSTOM_MSG_1908 = CustomMessageSettings.getString("CustomMsg1908", "");
			CUSTOM_MSG_1909 = CustomMessageSettings.getString("CustomMsg1909", "");
			CUSTOM_MSG_1910 = CustomMessageSettings.getString("CustomMsg1910", "");
			CUSTOM_MSG_1911 = CustomMessageSettings.getString("CustomMsg1911", "");
			CUSTOM_MSG_1912 = CustomMessageSettings.getString("CustomMsg1912", "");
			CUSTOM_MSG_1913 = CustomMessageSettings.getString("CustomMsg1913", "");
			CUSTOM_MSG_1914 = CustomMessageSettings.getString("CustomMsg1914", "");
			CUSTOM_MSG_1915 = CustomMessageSettings.getString("CustomMsg1915", "");
			CUSTOM_MSG_1916 = CustomMessageSettings.getString("CustomMsg1916", "");
			CUSTOM_MSG_1917 = CustomMessageSettings.getString("CustomMsg1917", "");
			CUSTOM_MSG_1918 = CustomMessageSettings.getString("CustomMsg1918", "");
			CUSTOM_MSG_1919 = CustomMessageSettings.getString("CustomMsg1919", "");
			CUSTOM_MSG_1920 = CustomMessageSettings.getString("CustomMsg1920", "");
			CUSTOM_MSG_1921 = CustomMessageSettings.getString("CustomMsg1921", "");
			CUSTOM_MSG_1922 = CustomMessageSettings.getString("CustomMsg1922", "");
			CUSTOM_MSG_1923 = CustomMessageSettings.getString("CustomMsg1923", "");
			CUSTOM_MSG_1924 = CustomMessageSettings.getString("CustomMsg1924", "");
			CUSTOM_MSG_1925 = CustomMessageSettings.getString("CustomMsg1925", "");
			CUSTOM_MSG_1926 = CustomMessageSettings.getString("CustomMsg1926", "");
			CUSTOM_MSG_1927 = CustomMessageSettings.getString("CustomMsg1927", "");
			CUSTOM_MSG_1928 = CustomMessageSettings.getString("CustomMsg1928", "");
			CUSTOM_MSG_1929 = CustomMessageSettings.getString("CustomMsg1929", "");
			CUSTOM_MSG_1930 = CustomMessageSettings.getString("CustomMsg1930", "");
			CUSTOM_MSG_1931 = CustomMessageSettings.getString("CustomMsg1931", "");
			CUSTOM_MSG_1932 = CustomMessageSettings.getString("CustomMsg1932", "");
			CUSTOM_MSG_1933 = CustomMessageSettings.getString("CustomMsg1933", "");
			CUSTOM_MSG_1934 = CustomMessageSettings.getString("CustomMsg1934", "");
			CUSTOM_MSG_1935 = CustomMessageSettings.getString("CustomMsg1935", "");
			CUSTOM_MSG_1936 = CustomMessageSettings.getString("CustomMsg1936", "");
			CUSTOM_MSG_1937 = CustomMessageSettings.getString("CustomMsg1937", "");
			CUSTOM_MSG_1938 = CustomMessageSettings.getString("CustomMsg1938", "");
			CUSTOM_MSG_1939 = CustomMessageSettings.getString("CustomMsg1939", "");
			CUSTOM_MSG_1940 = CustomMessageSettings.getString("CustomMsg1940", "");
			CUSTOM_MSG_1941 = CustomMessageSettings.getString("CustomMsg1941", "");
			CUSTOM_MSG_1942 = CustomMessageSettings.getString("CustomMsg1942", "");
			CUSTOM_MSG_1943 = CustomMessageSettings.getString("CustomMsg1943", "");
			CUSTOM_MSG_1944 = CustomMessageSettings.getString("CustomMsg1944", "");
			CUSTOM_MSG_1945 = CustomMessageSettings.getString("CustomMsg1945", "");
			CUSTOM_MSG_1946 = CustomMessageSettings.getString("CustomMsg1946", "");
			CUSTOM_MSG_1947 = CustomMessageSettings.getString("CustomMsg1947", "");
			CUSTOM_MSG_1948 = CustomMessageSettings.getString("CustomMsg1948", "");
			CUSTOM_MSG_1949 = CustomMessageSettings.getString("CustomMsg1949", "");
			CUSTOM_MSG_1950 = CustomMessageSettings.getString("CustomMsg1950", "");
			CUSTOM_MSG_1951 = CustomMessageSettings.getString("CustomMsg1951", "");
			CUSTOM_MSG_1952 = CustomMessageSettings.getString("CustomMsg1952", "");
			CUSTOM_MSG_1953 = CustomMessageSettings.getString("CustomMsg1953", "");
			CUSTOM_MSG_1954 = CustomMessageSettings.getString("CustomMsg1954", "");
			CUSTOM_MSG_1955 = CustomMessageSettings.getString("CustomMsg1955", "");
			CUSTOM_MSG_1956 = CustomMessageSettings.getString("CustomMsg1956", "");
			CUSTOM_MSG_1957 = CustomMessageSettings.getString("CustomMsg1957", "");
			CUSTOM_MSG_1958 = CustomMessageSettings.getString("CustomMsg1958", "");
			CUSTOM_MSG_1959 = CustomMessageSettings.getString("CustomMsg1959", "");
			CUSTOM_MSG_1960 = CustomMessageSettings.getString("CustomMsg1960", "");
			CUSTOM_MSG_1961 = CustomMessageSettings.getString("CustomMsg1961", "");
			CUSTOM_MSG_1962 = CustomMessageSettings.getString("CustomMsg1962", "");
			CUSTOM_MSG_1963 = CustomMessageSettings.getString("CustomMsg1963", "");
			CUSTOM_MSG_1964 = CustomMessageSettings.getString("CustomMsg1964", "");
			CUSTOM_MSG_1965 = CustomMessageSettings.getString("CustomMsg1965", "");
			CUSTOM_MSG_1966 = CustomMessageSettings.getString("CustomMsg1966", "");
			CUSTOM_MSG_1967 = CustomMessageSettings.getString("CustomMsg1967", "");
			CUSTOM_MSG_1968 = CustomMessageSettings.getString("CustomMsg1968", "");
			CUSTOM_MSG_1969 = CustomMessageSettings.getString("CustomMsg1969", "");
			CUSTOM_MSG_1970 = CustomMessageSettings.getString("CustomMsg1970", "");
			CUSTOM_MSG_1971 = CustomMessageSettings.getString("CustomMsg1971", "");
			CUSTOM_MSG_1972 = CustomMessageSettings.getString("CustomMsg1972", "");
			CUSTOM_MSG_1973 = CustomMessageSettings.getString("CustomMsg1973", "");
			CUSTOM_MSG_1974 = CustomMessageSettings.getString("CustomMsg1974", "");
			CUSTOM_MSG_1975 = CustomMessageSettings.getString("CustomMsg1975", "");
			CUSTOM_MSG_1976 = CustomMessageSettings.getString("CustomMsg1976", "");
			CUSTOM_MSG_1977 = CustomMessageSettings.getString("CustomMsg1977", "");
			CUSTOM_MSG_1978 = CustomMessageSettings.getString("CustomMsg1978", "");
			CUSTOM_MSG_1979 = CustomMessageSettings.getString("CustomMsg1979", "");
			CUSTOM_MSG_1980 = CustomMessageSettings.getString("CustomMsg1980", "");
			CUSTOM_MSG_1981 = CustomMessageSettings.getString("CustomMsg1981", "");
			CUSTOM_MSG_1982 = CustomMessageSettings.getString("CustomMsg1982", "");
			CUSTOM_MSG_1983 = CustomMessageSettings.getString("CustomMsg1983", "");
			CUSTOM_MSG_1984 = CustomMessageSettings.getString("CustomMsg1984", "");
			CUSTOM_MSG_1985 = CustomMessageSettings.getString("CustomMsg1985", "");
			CUSTOM_MSG_1986 = CustomMessageSettings.getString("CustomMsg1986", "");
			CUSTOM_MSG_1987 = CustomMessageSettings.getString("CustomMsg1987", "");
			CUSTOM_MSG_1988 = CustomMessageSettings.getString("CustomMsg1988", "");
			CUSTOM_MSG_1989 = CustomMessageSettings.getString("CustomMsg1989", "");
			CUSTOM_MSG_1990 = CustomMessageSettings.getString("CustomMsg1990", "");
			CUSTOM_MSG_1991 = CustomMessageSettings.getString("CustomMsg1991", "");
			CUSTOM_MSG_1992 = CustomMessageSettings.getString("CustomMsg1992", "");
			CUSTOM_MSG_1993 = CustomMessageSettings.getString("CustomMsg1993", "");
			CUSTOM_MSG_1994 = CustomMessageSettings.getString("CustomMsg1994", "");
			CUSTOM_MSG_1995 = CustomMessageSettings.getString("CustomMsg1995", "");
			CUSTOM_MSG_1996 = CustomMessageSettings.getString("CustomMsg1996", "");
			CUSTOM_MSG_1997 = CustomMessageSettings.getString("CustomMsg1997", "");
			CUSTOM_MSG_1998 = CustomMessageSettings.getString("CustomMsg1998", "");
			CUSTOM_MSG_1999 = CustomMessageSettings.getString("CustomMsg1999", "");
			CUSTOM_MSG_2000 = CustomMessageSettings.getString("CustomMsg2000", "");
			
			// Load Character L2Properties file (if exists)
			final PropertiesParser Character = new PropertiesParser(CHARACTER_CONFIG_FILE);
			
			ALT_GAME_DELEVEL = Character.getBoolean("Delevel", true);
			DECREASE_SKILL_LEVEL = Character.getBoolean("DecreaseSkillOnDelevel", true);
			ALT_WEIGHT_LIMIT = Character.getDouble("AltWeightLimit", 1);
			RUN_SPD_BOOST = Character.getInt("RunSpeedBoost", 0);
			DEATH_PENALTY_CHANCE = Character.getInt("DeathPenaltyChance", 20);
			RESPAWN_RESTORE_CP = Character.getDouble("RespawnRestoreCP", 0) / 100;
			RESPAWN_RESTORE_HP = Character.getDouble("RespawnRestoreHP", 65) / 100;
			RESPAWN_RESTORE_MP = Character.getDouble("RespawnRestoreMP", 0) / 100;
			HP_REGEN_MULTIPLIER = Character.getDouble("HpRegenMultiplier", 100) / 100;
			MP_REGEN_MULTIPLIER = Character.getDouble("MpRegenMultiplier", 100) / 100;
			CP_REGEN_MULTIPLIER = Character.getDouble("CpRegenMultiplier", 100) / 100;
			ALT_GAME_TIREDNESS = Character.getBoolean("AltGameTiredness", false);
			ENABLE_MODIFY_SKILL_DURATION = Character.getBoolean("EnableModifySkillDuration", false);
			
			// Create Map only if enabled
			if (ENABLE_MODIFY_SKILL_DURATION)
			{
				String[] propertySplit = Character.getString("SkillDurationList", "").split(";");
				SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					String[] skillSplit = skill.split(",");
					if (skillSplit.length != 2)
					{
						_log.warning(StringUtil.concat("[SkillDurationList]: invalid config property -> SkillDurationList \"", skill, "\""));
					}
					else
					{
						try
						{
							SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								_log.warning(StringUtil.concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
							}
						}
					}
				}
			}
			ENABLE_MODIFY_SKILL_REUSE = Character.getBoolean("EnableModifySkillReuse", false);
			// Create Map only if enabled
			if (ENABLE_MODIFY_SKILL_REUSE)
			{
				String[] propertySplit = Character.getString("SkillReuseList", "").split(";");
				SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					String[] skillSplit = skill.split(",");
					if (skillSplit.length != 2)
					{
						_log.warning(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\""));
					}
					else
					{
						try
						{
							SKILL_REUSE_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								_log.warning(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
							}
						}
					}
				}
			}
			
			AUTO_LEARN_SKILLS = Character.getBoolean("AutoLearnSkills", false);
			AUTO_LEARN_FS_SKILLS = Character.getBoolean("AutoLearnForgottenScrollSkills", false);
			AUTO_LOOT_HERBS = Character.getBoolean("AutoLootHerbs", false);
			BUFFS_MAX_AMOUNT = Character.getByte("MaxBuffAmount", (byte) 20);
			TRIGGERED_BUFFS_MAX_AMOUNT = Character.getByte("MaxTriggeredBuffAmount", (byte) 12);
			DANCES_MAX_AMOUNT = Character.getByte("MaxDanceAmount", (byte) 12);
			DANCE_CANCEL_BUFF = Character.getBoolean("DanceCancelBuff", false);
			DANCE_CONSUME_ADDITIONAL_MP = Character.getBoolean("DanceConsumeAdditionalMP", true);
			ALT_STORE_DANCES = Character.getBoolean("AltStoreDances", false);
			AUTO_LEARN_DIVINE_INSPIRATION = Character.getBoolean("AutoLearnDivineInspiration", false);
			ALT_GAME_CANCEL_BOW = Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			ALT_GAME_CANCEL_CAST = Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			ALT_GAME_MAGICFAILURES = Character.getBoolean("MagicFailures", true);
			PLAYER_FAKEDEATH_UP_PROTECTION = Character.getInt("PlayerFakeDeathUpProtection", 0);
			STORE_SKILL_COOLTIME = Character.getBoolean("StoreSkillCooltime", true);
			SUBCLASS_STORE_SKILL_COOLTIME = Character.getBoolean("SubclassStoreSkillCooltime", false);
			SUMMON_STORE_SKILL_COOLTIME = Character.getBoolean("SummonStoreSkillCooltime", true);
			ALT_GAME_SHIELD_BLOCKS = Character.getBoolean("AltShieldBlocks", false);
			ALT_PERFECT_SHLD_BLOCK = Character.getInt("AltPerfectShieldBlockRate", 10);
			EFFECT_TICK_RATIO = Character.getLong("EffectTickRatio", 666);
			ALLOW_CLASS_MASTERS = Character.getBoolean("AllowClassMasters", false);
			ALLOW_ENTIRE_TREE = Character.getBoolean("AllowEntireTree", false);
			ALTERNATE_CLASS_MASTER = Character.getBoolean("AlternateClassMaster", false);
			if (ALLOW_CLASS_MASTERS || ALTERNATE_CLASS_MASTER)
			{
				CLASS_MASTER_SETTINGS = new ClassMasterSettings(Character.getString("ConfigClassMaster", ""));
			}
			LIFE_CRYSTAL_NEEDED = Character.getBoolean("LifeCrystalNeeded", true);
			ES_SP_BOOK_NEEDED = Character.getBoolean("EnchantSkillSpBookNeeded", true);
			DIVINE_SP_BOOK_NEEDED = Character.getBoolean("DivineInspirationSpBookNeeded", true);
			ALT_GAME_SKILL_LEARN = Character.getBoolean("AltGameSkillLearn", false);
			ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Character.getBoolean("AltSubClassWithoutQuests", false);
			ALT_GAME_SUBCLASS_EVERYWHERE = Character.getBoolean("AltSubclassEverywhere", false);
			RESTORE_SERVITOR_ON_RECONNECT = Character.getBoolean("RestoreServitorOnReconnect", true);
			RESTORE_PET_ON_RECONNECT = Character.getBoolean("RestorePetOnReconnect", true);
			ALLOW_TRANSFORM_WITHOUT_QUEST = Character.getBoolean("AltTransformationWithoutQuest", false);
			FEE_DELETE_TRANSFER_SKILLS = Character.getInt("FeeDeleteTransferSkills", 10000000);
			FEE_DELETE_SUBCLASS_SKILLS = Character.getInt("FeeDeleteSubClassSkills", 10000000);
			ENABLE_VITALITY = Character.getBoolean("EnableVitality", true);
			RECOVER_VITALITY_ON_RECONNECT = Character.getBoolean("RecoverVitalityOnReconnect", true);
			STARTING_VITALITY_POINTS = Character.getInt("StartingVitalityPoints", 20000);
			MAX_BONUS_EXP = Character.getDouble("MaxExpBonus", 3.5);
			MAX_BONUS_SP = Character.getDouble("MaxSpBonus", 3.5);
			MAX_RUN_SPEED = Character.getInt("MaxRunSpeed", 250);
			MAX_PCRIT_RATE = Character.getInt("MaxPCritRate", 500);
			MAX_MCRIT_RATE = Character.getInt("MaxMCritRate", 200);
			MAX_PATK_SPEED = Character.getInt("MaxPAtkSpeed", 1500);
			MAX_MATK_SPEED = Character.getInt("MaxMAtkSpeed", 1999);
			MAX_EVASION = Character.getInt("MaxEvasion", 250);
			MIN_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MinAbnormalStateSuccessRate", 10);
			MAX_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MaxAbnormalStateSuccessRate", 90);
			MAX_SUBCLASS = Character.getByte("MaxSubclass", (byte) 3);
			BASE_SUBCLASS_LEVEL = Character.getByte("BaseSubclassLevel", (byte) 40);
			MAX_SUBCLASS_LEVEL = Character.getByte("MaxSubclassLevel", (byte) 80);
			MAX_PVTSTORESELL_SLOTS_DWARF = Character.getInt("MaxPvtStoreSellSlotsDwarf", 4);
			MAX_PVTSTORESELL_SLOTS_OTHER = Character.getInt("MaxPvtStoreSellSlotsOther", 3);
			MAX_PVTSTOREBUY_SLOTS_DWARF = Character.getInt("MaxPvtStoreBuySlotsDwarf", 5);
			MAX_PVTSTOREBUY_SLOTS_OTHER = Character.getInt("MaxPvtStoreBuySlotsOther", 4);
			INVENTORY_MAXIMUM_NO_DWARF = Character.getInt("MaximumSlotsForNoDwarf", 80);
			INVENTORY_MAXIMUM_DWARF = Character.getInt("MaximumSlotsForDwarf", 100);
			INVENTORY_MAXIMUM_GM = Character.getInt("MaximumSlotsForGMPlayer", 250);
			INVENTORY_MAXIMUM_QUEST_ITEMS = Character.getInt("MaximumSlotsForQuestItems", 100);
			MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
			WAREHOUSE_SLOTS_DWARF = Character.getInt("MaximumWarehouseSlotsForDwarf", 120);
			WAREHOUSE_SLOTS_NO_DWARF = Character.getInt("MaximumWarehouseSlotsForNoDwarf", 100);
			WAREHOUSE_SLOTS_CLAN = Character.getInt("MaximumWarehouseSlotsForClan", 150);
			ALT_FREIGHT_SLOTS = Character.getInt("MaximumFreightSlots", 200);
			ALT_FREIGHT_PRICE = Character.getInt("FreightPrice", 1000);
			ENCHANT_CHANCE_ELEMENT_STONE = Character.getDouble("EnchantChanceElementStone", 50);
			ENCHANT_CHANCE_ELEMENT_CRYSTAL = Character.getDouble("EnchantChanceElementCrystal", 30);
			ENCHANT_CHANCE_ELEMENT_JEWEL = Character.getDouble("EnchantChanceElementJewel", 20);
			ENCHANT_CHANCE_ELEMENT_ENERGY = Character.getDouble("EnchantChanceElementEnergy", 10);
			String[] notenchantable = Character.getString("EnchantBlackList", "7816,7817,7818,7819,7820,7821,7822,7823,7824,7825,7826,7827,7828,7829,7830,7831,13293,13294,13296").split(",");
			ENCHANT_BLACKLIST = new int[notenchantable.length];
			for (int i = 0; i < notenchantable.length; i++)
			{
				ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
			}
			Arrays.sort(ENCHANT_BLACKLIST);
			
			AUGMENTATION_NG_SKILL_CHANCE = Character.getInt("AugmentationNGSkillChance", 15);
			AUGMENTATION_NG_GLOW_CHANCE = Character.getInt("AugmentationNGGlowChance", 0);
			AUGMENTATION_MID_SKILL_CHANCE = Character.getInt("AugmentationMidSkillChance", 30);
			AUGMENTATION_MID_GLOW_CHANCE = Character.getInt("AugmentationMidGlowChance", 40);
			AUGMENTATION_HIGH_SKILL_CHANCE = Character.getInt("AugmentationHighSkillChance", 45);
			AUGMENTATION_HIGH_GLOW_CHANCE = Character.getInt("AugmentationHighGlowChance", 70);
			AUGMENTATION_TOP_SKILL_CHANCE = Character.getInt("AugmentationTopSkillChance", 60);
			AUGMENTATION_TOP_GLOW_CHANCE = Character.getInt("AugmentationTopGlowChance", 100);
			AUGMENTATION_BASESTAT_CHANCE = Character.getInt("AugmentationBaseStatChance", 1);
			AUGMENTATION_ACC_SKILL_CHANCE = Character.getInt("AugmentationAccSkillChance", 0);
			
			RETAIL_LIKE_AUGMENTATION = Character.getBoolean("RetailLikeAugmentation", true);
			String[] array = Character.getString("RetailLikeAugmentationNoGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_NG_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_NG_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getString("RetailLikeAugmentationMidGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_MID_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_MID_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getString("RetailLikeAugmentationHighGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getString("RetailLikeAugmentationTopGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_TOP_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_TOP_CHANCE[i] = Integer.parseInt(array[i]);
			}
			RETAIL_LIKE_AUGMENTATION_ACCESSORY = Character.getBoolean("RetailLikeAugmentationAccessory", true);
			
			array = Character.getString("AugmentationBlackList", "6656,6657,6658,6659,6660,6661,6662,8191,10170,10314,13740,13741,13742,13743,13744,13745,13746,13747,13748,14592,14593,14594,14595,14596,14597,14598,14599,14600,14664,14665,14666,14667,14668,14669,14670,14671,14672,14801,14802,14803,14804,14805,14806,14807,14808,14809,15282,15283,15284,15285,15286,15287,15288,15289,15290,15291,15292,15293,15294,15295,15296,15297,15298,15299,16025,16026,21712,22173,22174,22175").split(",");
			AUGMENTATION_BLACKLIST = new int[array.length];
			
			for (int i = 0; i < array.length; i++)
			{
				AUGMENTATION_BLACKLIST[i] = Integer.parseInt(array[i]);
			}
			
			Arrays.sort(AUGMENTATION_BLACKLIST);
			ALT_ALLOW_AUGMENT_PVP_ITEMS = Character.getBoolean("AltAllowAugmentPvPItems", false);
			ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Character.getBoolean("AltKarmaPlayerCanBeKilledInPeaceZone", false);
			ALT_GAME_KARMA_PLAYER_CAN_SHOP = Character.getBoolean("AltKarmaPlayerCanShop", true);
			ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Character.getBoolean("AltKarmaPlayerCanTeleport", true);
			ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Character.getBoolean("AltKarmaPlayerCanUseGK", false);
			ALT_GAME_KARMA_PLAYER_CAN_TRADE = Character.getBoolean("AltKarmaPlayerCanTrade", true);
			ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Character.getBoolean("AltKarmaPlayerCanUseWareHouse", true);
			MAX_PERSONAL_FAME_POINTS = Character.getInt("MaxPersonalFamePoints", 100000);
			FORTRESS_ZONE_FAME_TASK_FREQUENCY = Character.getInt("FortressZoneFameTaskFrequency", 300);
			FORTRESS_ZONE_FAME_AQUIRE_POINTS = Character.getInt("FortressZoneFameAquirePoints", 31);
			CASTLE_ZONE_FAME_TASK_FREQUENCY = Character.getInt("CastleZoneFameTaskFrequency", 300);
			CASTLE_ZONE_FAME_AQUIRE_POINTS = Character.getInt("CastleZoneFameAquirePoints", 125);
			FAME_FOR_DEAD_PLAYERS = Character.getBoolean("FameForDeadPlayers", true);
			IS_CRAFTING_ENABLED = Character.getBoolean("CraftingEnabled", true);
			CRAFT_MASTERWORK = Character.getBoolean("CraftMasterwork", true);
			DWARF_RECIPE_LIMIT = Character.getInt("DwarfRecipeLimit", 50);
			COMMON_RECIPE_LIMIT = Character.getInt("CommonRecipeLimit", 50);
			ALT_GAME_CREATION = Character.getBoolean("AltGameCreation", false);
			ALT_GAME_CREATION_SPEED = Character.getDouble("AltGameCreationSpeed", 1);
			ALT_GAME_CREATION_XP_RATE = Character.getDouble("AltGameCreationXpRate", 1);
			ALT_GAME_CREATION_SP_RATE = Character.getDouble("AltGameCreationSpRate", 1);
			ALT_GAME_CREATION_RARE_XPSP_RATE = Character.getDouble("AltGameCreationRareXpSpRate", 2);
			ALT_BLACKSMITH_USE_RECIPES = Character.getBoolean("AltBlacksmithUseRecipes", true);
			ALT_CLAN_LEADER_DATE_CHANGE = Character.getInt("AltClanLeaderDateChange", 3);
			if ((ALT_CLAN_LEADER_DATE_CHANGE < 1) || (ALT_CLAN_LEADER_DATE_CHANGE > 7))
			{
				_log.log(Level.WARNING, "Wrong value specified for AltClanLeaderDateChange: " + ALT_CLAN_LEADER_DATE_CHANGE);
				ALT_CLAN_LEADER_DATE_CHANGE = 3;
			}
			ALT_CLAN_LEADER_HOUR_CHANGE = Character.getString("AltClanLeaderHourChange", "00:00:00");
			ALT_CLAN_LEADER_INSTANT_ACTIVATION = Character.getBoolean("AltClanLeaderInstantActivation", false);
			ALT_CLAN_JOIN_DAYS = Character.getInt("DaysBeforeJoinAClan", 1);
			ALT_CLAN_CREATE_DAYS = Character.getInt("DaysBeforeCreateAClan", 10);
			ALT_CLAN_DISSOLVE_DAYS = Character.getInt("DaysToPassToDissolveAClan", 7);
			ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Character.getInt("DaysBeforeJoinAllyWhenLeaved", 1);
			ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeJoinAllyWhenDismissed", 1);
			ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeAcceptNewClanWhenDismissed", 1);
			ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Character.getInt("DaysBeforeCreateNewAllyWhenDissolved", 1);
			ALT_MAX_NUM_OF_CLANS_IN_ALLY = Character.getInt("AltMaxNumOfClansInAlly", 3);
			ALT_CLAN_MEMBERS_FOR_WAR = Character.getInt("AltClanMembersForWar", 15);
			ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Character.getBoolean("AltMembersCanWithdrawFromClanWH", false);
			REMOVE_CASTLE_CIRCLETS = Character.getBoolean("RemoveCastleCirclets", true);
			ALT_PARTY_RANGE = Character.getInt("AltPartyRange", 1600);
			ALT_PARTY_RANGE2 = Character.getInt("AltPartyRange2", 1400);
			ALT_LEAVE_PARTY_LEADER = Character.getBoolean("AltLeavePartyLeader", false);
			INITIAL_EQUIPMENT_EVENT = Character.getBoolean("InitialEquipmentEvent", false);
			STARTING_ADENA = Character.getLong("StartingAdena", 0);
			STARTING_LEVEL = Character.getByte("StartingLevel", (byte) 1);
			STARTING_SP = Character.getInt("StartingSP", 0);
			MAX_ADENA = Character.getLong("MaxAdena", 99900000000L);
			if (MAX_ADENA < 0)
			{
				MAX_ADENA = Long.MAX_VALUE;
			}
			AUTO_LOOT = Character.getBoolean("AutoLoot", false);
			AUTO_LOOT_RAIDS = Character.getBoolean("AutoLootRaids", false);
			LOOT_RAIDS_PRIVILEGE_INTERVAL = Character.getInt("RaidLootRightsInterval", 900) * 1000;
			LOOT_RAIDS_PRIVILEGE_CC_SIZE = Character.getInt("RaidLootRightsCCSize", 45);
			UNSTUCK_INTERVAL = Character.getInt("UnstuckInterval", 300);
			TELEPORT_WATCHDOG_TIMEOUT = Character.getInt("TeleportWatchdogTimeout", 0);
			PLAYER_SPAWN_PROTECTION = Character.getInt("PlayerSpawnProtection", 0);
			String[] items = Character.getString("PlayerSpawnProtectionAllowedItems", "0").split(",");
			SPAWN_PROTECTION_ALLOWED_ITEMS = new ArrayList<>(items.length);
			for (String item : items)
			{
				Integer itm = 0;
				try
				{
					itm = Integer.parseInt(item);
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("Player Spawn Protection: Wrong ItemId passed: " + item);
					_log.warning(nfe.getMessage());
				}
				if (itm != 0)
				{
					SPAWN_PROTECTION_ALLOWED_ITEMS.add(itm);
				}
			}
			PLAYER_TELEPORT_PROTECTION = Character.getInt("PlayerTeleportProtection", 0);
			RANDOM_RESPAWN_IN_TOWN_ENABLED = Character.getBoolean("RandomRespawnInTownEnabled", true);
			OFFSET_ON_TELEPORT_ENABLED = Character.getBoolean("OffsetOnTeleportEnabled", true);
			MAX_OFFSET_ON_TELEPORT = Character.getInt("MaxOffsetOnTeleport", 50);
			PETITIONING_ALLOWED = Character.getBoolean("PetitioningAllowed", true);
			MAX_PETITIONS_PER_PLAYER = Character.getInt("MaxPetitionsPerPlayer", 5);
			MAX_PETITIONS_PENDING = Character.getInt("MaxPetitionsPending", 25);
			ALT_GAME_FREE_TELEPORT = Character.getBoolean("AltFreeTeleporting", false);
			DELETE_DAYS = Character.getInt("DeleteCharAfterDays", 7);
			ALT_GAME_EXPONENT_XP = Character.getFloat("AltGameExponentXp", 0);
			ALT_GAME_EXPONENT_SP = Character.getFloat("AltGameExponentSp", 0);
			PARTY_XP_CUTOFF_METHOD = Character.getString("PartyXpCutoffMethod", "highfive");
			PARTY_XP_CUTOFF_PERCENT = Character.getDouble("PartyXpCutoffPercent", 3);
			PARTY_XP_CUTOFF_LEVEL = Character.getInt("PartyXpCutoffLevel", 20);
			final String[] gaps = Character.getString("PartyXpCutoffGaps", "0,9;10,14;15,99").split(";");
			PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
			for (int i = 0; i < gaps.length; i++)
			{
				PARTY_XP_CUTOFF_GAPS[i] = new int[]
				{
					Integer.parseInt(gaps[i].split(",")[0]),
					Integer.parseInt(gaps[i].split(",")[1])
				};
			}
			final String[] percents = Character.getString("PartyXpCutoffGapPercent", "100;30;0").split(";");
			PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
			for (int i = 0; i < percents.length; i++)
			{
				PARTY_XP_CUTOFF_GAP_PERCENTS[i] = Integer.parseInt(percents[i]);
			}
			DISABLE_TUTORIAL = Character.getBoolean("DisableTutorial", false);
			EXPERTISE_PENALTY = Character.getBoolean("ExpertisePenalty", true);
			STORE_RECIPE_SHOPLIST = Character.getBoolean("StoreRecipeShopList", false);
			STORE_UI_SETTINGS = Character.getBoolean("StoreCharUiSettings", false);
			FORBIDDEN_NAMES = Character.getString("ForbiddenNames", "").split(",");
			SILENCE_MODE_EXCLUDE = Character.getBoolean("SilenceModeExclude", false);
			ALT_VALIDATE_TRIGGER_SKILLS = Character.getBoolean("AltValidateTriggerSkills", false);
			PLAYER_MOVEMENT_BLOCK_TIME = Character.getInt("NpcTalkBlockingTime", 0) * 1000;
			
			// Load Telnet L2Properties file (if exists)
			final PropertiesParser telnetSettings = new PropertiesParser(TELNET_FILE);
			
			IS_TELNET_ENABLED = telnetSettings.getBoolean("EnableTelnet", false);
			
			// MMO
			final PropertiesParser mmoSettings = new PropertiesParser(MMO_CONFIG_FILE);
			
			MMO_SELECTOR_SLEEP_TIME = mmoSettings.getInt("SleepTime", 20);
			MMO_MAX_SEND_PER_PASS = mmoSettings.getInt("MaxSendPerPass", 12);
			MMO_MAX_READ_PER_PASS = mmoSettings.getInt("MaxReadPerPass", 12);
			MMO_HELPER_BUFFER_COUNT = mmoSettings.getInt("HelperBufferCount", 20);
			MMO_TCP_NODELAY = mmoSettings.getBoolean("TcpNoDelay", false);
			
			// Load IdFactory L2Properties file (if exists)
			final PropertiesParser IdFactory = new PropertiesParser(ID_CONFIG_FILE);
			
			IDFACTORY_TYPE = IdFactory.getEnum("IDFactory", IdFactoryType.class, IdFactoryType.BitSet);
			BAD_ID_CHECKING = IdFactory.getBoolean("BadIdChecking", true);
			
			// Load General L2Properties file (if exists)
			final PropertiesParser General = new PropertiesParser(GENERAL_CONFIG_FILE);
			EVERYBODY_HAS_ADMIN_RIGHTS = General.getBoolean("EverybodyHasAdminRights", false);
			SERVER_LIST_BRACKET = General.getBoolean("ServerListBrackets", false);
			SERVER_LIST_TYPE = getServerTypeId(General.getString("ServerListType", "Normal").split(","));
			SERVER_LIST_AGE = General.getInt("ServerListAge", 0);
			SERVER_GMONLY = General.getBoolean("ServerGMOnly", false);
			GM_HERO_AURA = General.getBoolean("GMHeroAura", false);
			GM_STARTUP_INVULNERABLE = General.getBoolean("GMStartupInvulnerable", false);
			GM_STARTUP_INVISIBLE = General.getBoolean("GMStartupInvisible", false);
			GM_STARTUP_SILENCE = General.getBoolean("GMStartupSilence", false);
			GM_STARTUP_AUTO_LIST = General.getBoolean("GMStartupAutoList", false);
			GM_STARTUP_DIET_MODE = General.getBoolean("GMStartupDietMode", false);
			GM_ITEM_RESTRICTION = General.getBoolean("GMItemRestriction", true);
			GM_SKILL_RESTRICTION = General.getBoolean("GMSkillRestriction", true);
			GM_TRADE_RESTRICTED_ITEMS = General.getBoolean("GMTradeRestrictedItems", false);
			GM_RESTART_FIGHTING = General.getBoolean("GMRestartFighting", true);
			GM_ANNOUNCER_NAME = General.getBoolean("GMShowAnnouncerName", false);
			GM_CRITANNOUNCER_NAME = General.getBoolean("GMShowCritAnnouncerName", false);
			GM_GIVE_SPECIAL_SKILLS = General.getBoolean("GMGiveSpecialSkills", false);
			GM_GIVE_SPECIAL_AURA_SKILLS = General.getBoolean("GMGiveSpecialAuraSkills", false);
			GAMEGUARD_ENFORCE = General.getBoolean("GameGuardEnforce", false);
			GAMEGUARD_PROHIBITACTION = General.getBoolean("GameGuardProhibitAction", false);
			LOG_CHAT = General.getBoolean("LogChat", false);
			LOG_AUTO_ANNOUNCEMENTS = General.getBoolean("LogAutoAnnouncements", false);
			LOG_ITEMS = General.getBoolean("LogItems", false);
			LOG_ITEMS_SMALL_LOG = General.getBoolean("LogItemsSmallLog", false);
			LOG_ITEM_ENCHANTS = General.getBoolean("LogItemEnchants", false);
			LOG_SKILL_ENCHANTS = General.getBoolean("LogSkillEnchants", false);
			GMAUDIT = General.getBoolean("GMAudit", false);
			SKILL_CHECK_ENABLE = General.getBoolean("SkillCheckEnable", false);
			SKILL_CHECK_REMOVE = General.getBoolean("SkillCheckRemove", false);
			SKILL_CHECK_GM = General.getBoolean("SkillCheckGM", true);
			DEBUG = General.getBoolean("Debug", false);
			DEBUG_INSTANCES = General.getBoolean("InstanceDebug", false);
			HTML_ACTION_CACHE_DEBUG = General.getBoolean("HtmlActionCacheDebug", false);
			PACKET_HANDLER_DEBUG = General.getBoolean("PacketHandlerDebug", false);
			DEVELOPER = General.getBoolean("Developer", false);
			ALT_DEV_NO_HANDLERS = General.getBoolean("AltDevNoHandlers", false) || Boolean.getBoolean("nohandlers");
			ALT_DEV_NO_QUESTS = General.getBoolean("AltDevNoQuests", false) || Boolean.getBoolean("noquests");
			ALT_DEV_NO_SPAWNS = General.getBoolean("AltDevNoSpawns", false) || Boolean.getBoolean("nospawns");
			ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowQuestsLoadInLogs", false);
			ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowScriptsLoadInLogs", false);
			THREAD_P_EFFECTS = General.getInt("ThreadPoolSizeEffects", 10);
			THREAD_P_GENERAL = General.getInt("ThreadPoolSizeGeneral", 13);
			THREAD_E_EVENTS = General.getInt("ThreadPoolSizeEvents", 2);
			IO_PACKET_THREAD_CORE_SIZE = General.getInt("UrgentPacketThreadCoreSize", 2);
			GENERAL_PACKET_THREAD_CORE_SIZE = General.getInt("GeneralPacketThreadCoreSize", 4);
			GENERAL_THREAD_CORE_SIZE = General.getInt("GeneralThreadCoreSize", 4);
			AI_MAX_THREAD = General.getInt("AiMaxThread", 6);
			EVENT_MAX_THREAD = General.getInt("EventsMaxThread", 5);
			CLIENT_PACKET_QUEUE_SIZE = General.getInt("ClientPacketQueueSize", 0);
			if (CLIENT_PACKET_QUEUE_SIZE == 0)
			{
				CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2;
			}
			CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = General.getInt("ClientPacketQueueMaxBurstSize", 0);
			if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
			{
				CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1;
			}
			CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = General.getInt("ClientPacketQueueMaxPacketsPerSecond", 80);
			CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = General.getInt("ClientPacketQueueMeasureInterval", 5);
			CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = General.getInt("ClientPacketQueueMaxAveragePacketsPerSecond", 40);
			CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = General.getInt("ClientPacketQueueMaxFloodsPerMin", 2);
			CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = General.getInt("ClientPacketQueueMaxOverflowsPerMin", 1);
			CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = General.getInt("ClientPacketQueueMaxUnderflowsPerMin", 1);
			CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = General.getInt("ClientPacketQueueMaxUnknownPerMin", 5);
			DEADLOCK_DETECTOR = General.getBoolean("DeadLockDetector", true);
			DEADLOCK_CHECK_INTERVAL = General.getInt("DeadLockCheckInterval", 20);
			RESTART_ON_DEADLOCK = General.getBoolean("RestartOnDeadlock", false);
			ALLOW_DISCARDITEM = General.getBoolean("AllowDiscardItem", true);
			AUTODESTROY_ITEM_AFTER = General.getInt("AutoDestroyDroppedItemAfter", 600);
			HERB_AUTO_DESTROY_TIME = General.getInt("AutoDestroyHerbTime", 60) * 1000;
			String[] split = General.getString("ListOfProtectedItems", "0").split(",");
			LIST_PROTECTED_ITEMS = new ArrayList<>(split.length);
			for (String id : split)
			{
				LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
			}
			DATABASE_CLEAN_UP = General.getBoolean("DatabaseCleanUp", true);
			CONNECTION_CLOSE_TIME = General.getLong("ConnectionCloseTime", 60000);
			CHAR_STORE_INTERVAL = General.getInt("CharacterDataStoreInterval", 15);
			LAZY_ITEMS_UPDATE = General.getBoolean("LazyItemsUpdate", false);
			UPDATE_ITEMS_ON_CHAR_STORE = General.getBoolean("UpdateItemsOnCharStore", false);
			DESTROY_DROPPED_PLAYER_ITEM = General.getBoolean("DestroyPlayerDroppedItem", false);
			DESTROY_EQUIPABLE_PLAYER_ITEM = General.getBoolean("DestroyEquipableItem", false);
			SAVE_DROPPED_ITEM = General.getBoolean("SaveDroppedItem", false);
			EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = General.getBoolean("EmptyDroppedItemTableAfterLoad", false);
			SAVE_DROPPED_ITEM_INTERVAL = General.getInt("SaveDroppedItemInterval", 60) * 60000;
			CLEAR_DROPPED_ITEM_TABLE = General.getBoolean("ClearDroppedItemTable", false);
			AUTODELETE_INVALID_QUEST_DATA = General.getBoolean("AutoDeleteInvalidQuestData", false);
			PRECISE_DROP_CALCULATION = General.getBoolean("PreciseDropCalculation", true);
			MULTIPLE_ITEM_DROP = General.getBoolean("MultipleItemDrop", true);
			FORCE_INVENTORY_UPDATE = General.getBoolean("ForceInventoryUpdate", false);
			LAZY_CACHE = General.getBoolean("LazyCache", true);
			CACHE_CHAR_NAMES = General.getBoolean("CacheCharNames", true);
			MIN_NPC_ANIMATION = General.getInt("MinNPCAnimation", 10);
			MAX_NPC_ANIMATION = General.getInt("MaxNPCAnimation", 20);
			MIN_MONSTER_ANIMATION = General.getInt("MinMonsterAnimation", 5);
			MAX_MONSTER_ANIMATION = General.getInt("MaxMonsterAnimation", 20);
			MOVE_BASED_KNOWNLIST = General.getBoolean("MoveBasedKnownlist", false);
			KNOWNLIST_UPDATE_INTERVAL = General.getLong("KnownListUpdateInterval", 1250);
			GRIDS_ALWAYS_ON = General.getBoolean("GridsAlwaysOn", false);
			GRID_NEIGHBOR_TURNON_TIME = General.getInt("GridNeighborTurnOnTime", 1);
			GRID_NEIGHBOR_TURNOFF_TIME = General.getInt("GridNeighborTurnOffTime", 90);
			PEACE_ZONE_MODE = General.getInt("PeaceZoneMode", 0);
			DEFAULT_GLOBAL_CHAT = General.getString("GlobalChat", "ON");
			DEFAULT_TRADE_CHAT = General.getString("TradeChat", "ON");
			ALLOW_WAREHOUSE = General.getBoolean("AllowWarehouse", true);
			WAREHOUSE_CACHE = General.getBoolean("WarehouseCache", false);
			WAREHOUSE_CACHE_TIME = General.getInt("WarehouseCacheTime", 15);
			ALLOW_REFUND = General.getBoolean("AllowRefund", true);
			ALLOW_MAIL = General.getBoolean("AllowMail", true);
			ALLOW_ATTACHMENTS = General.getBoolean("AllowAttachments", true);
			ALLOW_WEAR = General.getBoolean("AllowWear", true);
			WEAR_DELAY = General.getInt("WearDelay", 5);
			WEAR_PRICE = General.getInt("WearPrice", 10);
			INSTANCE_FINISH_TIME = 1000 * General.getInt("DefaultFinishTime", 300);
			RESTORE_PLAYER_INSTANCE = General.getBoolean("RestorePlayerInstance", false);
			ALLOW_SUMMON_IN_INSTANCE = General.getBoolean("AllowSummonInInstance", false);
			EJECT_DEAD_PLAYER_TIME = 1000 * General.getInt("EjectDeadPlayerTime", 60);
			ALLOW_LOTTERY = General.getBoolean("AllowLottery", true);
			ALLOW_RACE = General.getBoolean("AllowRace", true);
			ALLOW_WATER = General.getBoolean("AllowWater", true);
			ALLOW_RENTPET = General.getBoolean("AllowRentPet", false);
			ALLOWFISHING = General.getBoolean("AllowFishing", true);
			ALLOW_MANOR = General.getBoolean("AllowManor", true);
			ALLOW_BOAT = General.getBoolean("AllowBoat", true);
			BOAT_BROADCAST_RADIUS = General.getInt("BoatBroadcastRadius", 20000);
			ALLOW_CURSED_WEAPONS = General.getBoolean("AllowCursedWeapons", true);
			ALLOW_PET_WALKERS = General.getBoolean("AllowPetWalkers", true);
			SERVER_NEWS = General.getBoolean("ShowServerNews", false);
			ENABLE_COMMUNITY_BOARD = General.getBoolean("EnableCommunityBoard", true);
			BBS_DEFAULT = General.getString("BBSDefault", "_bbshome");
			USE_SAY_FILTER = General.getBoolean("UseChatFilter", false);
			CHAT_FILTER_CHARS = General.getString("ChatFilterChars", "^_^");
			String[] propertySplit4 = General.getString("BanChatChannels", "0;1;8;17").trim().split(";");
			BAN_CHAT_CHANNELS = new int[propertySplit4.length];
			try
			{
				int i = 0;
				for (String chatId : propertySplit4)
				{
					BAN_CHAT_CHANNELS[i++] = Integer.parseInt(chatId);
				}
			}
			catch (NumberFormatException nfe)
			{
				_log.log(Level.WARNING, nfe.getMessage(), nfe);
			}
			ALT_MANOR_REFRESH_TIME = General.getInt("AltManorRefreshTime", 20);
			ALT_MANOR_REFRESH_MIN = General.getInt("AltManorRefreshMin", 0);
			ALT_MANOR_APPROVE_TIME = General.getInt("AltManorApproveTime", 4);
			ALT_MANOR_APPROVE_MIN = General.getInt("AltManorApproveMin", 30);
			ALT_MANOR_MAINTENANCE_MIN = General.getInt("AltManorMaintenanceMin", 6);
			ALT_MANOR_SAVE_ALL_ACTIONS = General.getBoolean("AltManorSaveAllActions", false);
			ALT_MANOR_SAVE_PERIOD_RATE = General.getInt("AltManorSavePeriodRate", 2);
			ALT_LOTTERY_PRIZE = General.getLong("AltLotteryPrize", 50000);
			ALT_LOTTERY_TICKET_PRICE = General.getLong("AltLotteryTicketPrice", 2000);
			ALT_LOTTERY_5_NUMBER_RATE = General.getFloat("AltLottery5NumberRate", 0.6f);
			ALT_LOTTERY_4_NUMBER_RATE = General.getFloat("AltLottery4NumberRate", 0.2f);
			ALT_LOTTERY_3_NUMBER_RATE = General.getFloat("AltLottery3NumberRate", 0.2f);
			ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = General.getLong("AltLottery2and1NumberPrize", 200);
			ALT_ITEM_AUCTION_ENABLED = General.getBoolean("AltItemAuctionEnabled", true);
			ALT_ITEM_AUCTION_EXPIRED_AFTER = General.getInt("AltItemAuctionExpiredAfter", 14);
			ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID = General.getInt("AltItemAuctionTimeExtendsOnBid", 0) * 1000;
			FS_TIME_ATTACK = General.getInt("TimeOfAttack", 50);
			FS_TIME_COOLDOWN = General.getInt("TimeOfCoolDown", 5);
			FS_TIME_ENTRY = General.getInt("TimeOfEntry", 3);
			FS_TIME_WARMUP = General.getInt("TimeOfWarmUp", 2);
			FS_PARTY_MEMBER_COUNT = General.getInt("NumberOfNecessaryPartyMembers", 4);
			if (FS_TIME_ATTACK <= 0)
			{
				FS_TIME_ATTACK = 50;
			}
			if (FS_TIME_COOLDOWN <= 0)
			{
				FS_TIME_COOLDOWN = 5;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			RIFT_MIN_PARTY_SIZE = General.getInt("RiftMinPartySize", 5);
			RIFT_MAX_JUMPS = General.getInt("MaxRiftJumps", 4);
			RIFT_SPAWN_DELAY = General.getInt("RiftSpawnDelay", 10000);
			RIFT_AUTO_JUMPS_TIME_MIN = General.getInt("AutoJumpsDelayMin", 480);
			RIFT_AUTO_JUMPS_TIME_MAX = General.getInt("AutoJumpsDelayMax", 600);
			RIFT_BOSS_ROOM_TIME_MUTIPLY = General.getFloat("BossRoomTimeMultiply", 1.5f);
			RIFT_ENTER_COST_RECRUIT = General.getInt("RecruitCost", 18);
			RIFT_ENTER_COST_SOLDIER = General.getInt("SoldierCost", 21);
			RIFT_ENTER_COST_OFFICER = General.getInt("OfficerCost", 24);
			RIFT_ENTER_COST_CAPTAIN = General.getInt("CaptainCost", 27);
			RIFT_ENTER_COST_COMMANDER = General.getInt("CommanderCost", 30);
			RIFT_ENTER_COST_HERO = General.getInt("HeroCost", 33);
			DEFAULT_PUNISH = IllegalActionPunishmentType.findByName(General.getString("DefaultPunish", "KICK"));
			DEFAULT_PUNISH_PARAM = General.getInt("DefaultPunishParam", 0);
			ONLY_GM_ITEMS_FREE = General.getBoolean("OnlyGMItemsFree", true);
			JAIL_IS_PVP = General.getBoolean("JailIsPvp", false);
			JAIL_DISABLE_CHAT = General.getBoolean("JailDisableChat", true);
			JAIL_DISABLE_TRANSACTION = General.getBoolean("JailDisableTransaction", false);
			CUSTOM_SPAWNLIST_TABLE = General.getBoolean("CustomSpawnlistTable", false);
			SAVE_GMSPAWN_ON_CUSTOM = General.getBoolean("SaveGmSpawnOnCustom", false);
			CUSTOM_NPC_DATA = General.getBoolean("CustomNpcData", false);
			CUSTOM_TELEPORT_TABLE = General.getBoolean("CustomTeleportTable", false);
			CUSTOM_NPCBUFFER_TABLES = General.getBoolean("CustomNpcBufferTables", false);
			CUSTOM_SKILLS_LOAD = General.getBoolean("CustomSkillsLoad", false);
			CUSTOM_ITEMS_LOAD = General.getBoolean("CustomItemsLoad", false);
			CUSTOM_MULTISELL_LOAD = General.getBoolean("CustomMultisellLoad", false);
			CUSTOM_BUYLIST_LOAD = General.getBoolean("CustomBuyListLoad", false);
			ALT_BIRTHDAY_GIFT = General.getInt("AltBirthdayGift", 22187);
			ALT_BIRTHDAY_MAIL_SUBJECT = General.getString("AltBirthdayMailSubject", "Happy Birthday!");
			ALT_BIRTHDAY_MAIL_TEXT = General.getString("AltBirthdayMailText", "Hello Adventurer!! Seeing as you're one year older now, I thought I would send you some birthday cheer :) Please find your birthday pack attached. May these gifts bring you joy and happiness on this very special day." + EOL + EOL + "Sincerely, Alegria");
			ENABLE_BLOCK_CHECKER_EVENT = General.getBoolean("EnableBlockCheckerEvent", false);
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = General.getInt("BlockCheckerMinTeamMembers", 2);
			if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1)
			{
				MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
			}
			else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6)
			{
				MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
			}
			HBCE_FAIR_PLAY = General.getBoolean("HBCEFairPlay", false);
			HELLBOUND_WITHOUT_QUEST = General.getBoolean("HellboundWithoutQuest", false);
			
			NORMAL_ENCHANT_COST_MULTIPLIER = General.getInt("NormalEnchantCostMultipiler", 1);
			SAFE_ENCHANT_COST_MULTIPLIER = General.getInt("SafeEnchantCostMultipiler", 5);
			
			BOTREPORT_ENABLE = General.getBoolean("EnableBotReportButton", false);
			BOTREPORT_RESETPOINT_HOUR = General.getString("BotReportPointsResetHour", "00:00").split(":");
			BOTREPORT_REPORT_DELAY = General.getInt("BotReportDelay", 30) * 60000;
			BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = General.getBoolean("AllowReportsFromSameClanMembers", false);
			ENABLE_FALLING_DAMAGE = General.getBoolean("EnableFallingDamage", true);
			
			// Load FloodProtector L2Properties file
			final PropertiesParser FloodProtectors = new PropertiesParser(FLOOD_PROTECTOR_FILE);
			
			loadFloodProtectorConfigs(FloodProtectors);
			
			// Load NPC L2Properties file (if exists)
			final PropertiesParser NPC = new PropertiesParser(NPC_CONFIG_FILE);
			
			ANNOUNCE_MAMMON_SPAWN = NPC.getBoolean("AnnounceMammonSpawn", false);
			ALT_MOB_AGRO_IN_PEACEZONE = NPC.getBoolean("AltMobAgroInPeaceZone", true);
			ALT_ATTACKABLE_NPCS = NPC.getBoolean("AltAttackableNpcs", true);
			ALT_GAME_VIEWNPC = NPC.getBoolean("AltGameViewNpc", false);
			MAX_DRIFT_RANGE = NPC.getInt("MaxDriftRange", 300);
			DEEPBLUE_DROP_RULES = NPC.getBoolean("UseDeepBlueDropRules", true);
			DEEPBLUE_DROP_RULES_RAID = NPC.getBoolean("UseDeepBlueDropRulesRaid", true);
			SHOW_NPC_LVL = NPC.getBoolean("ShowNpcLevel", false);
			SHOW_CREST_WITHOUT_QUEST = NPC.getBoolean("ShowCrestWithoutQuest", false);
			ENABLE_RANDOM_ENCHANT_EFFECT = NPC.getBoolean("EnableRandomEnchantEffect", false);
			MIN_NPC_LVL_DMG_PENALTY = NPC.getInt("MinNPCLevelForDmgPenalty", 78);
			NPC_DMG_PENALTY = parseConfigLine(NPC.getString("DmgPenaltyForLvLDifferences", "0.7, 0.6, 0.6, 0.55"));
			NPC_CRIT_DMG_PENALTY = parseConfigLine(NPC.getString("CritDmgPenaltyForLvLDifferences", "0.75, 0.65, 0.6, 0.58"));
			NPC_SKILL_DMG_PENALTY = parseConfigLine(NPC.getString("SkillDmgPenaltyForLvLDifferences", "0.8, 0.7, 0.65, 0.62"));
			MIN_NPC_LVL_MAGIC_PENALTY = NPC.getInt("MinNPCLevelForMagicPenalty", 78);
			NPC_SKILL_CHANCE_PENALTY = parseConfigLine(NPC.getString("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5"));
			DECAY_TIME_TASK = NPC.getInt("DecayTimeTask", 5000);
			DEFAULT_CORPSE_TIME = NPC.getInt("DefaultCorpseTime", 7);
			SPOILED_CORPSE_EXTEND_TIME = NPC.getInt("SpoiledCorpseExtendTime", 10);
			CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY = NPC.getInt("CorpseConsumeSkillAllowedTimeBeforeDecay", 2000);
			GUARD_ATTACK_AGGRO_MOB = NPC.getBoolean("GuardAttackAggroMob", false);
			ALLOW_WYVERN_UPGRADER = NPC.getBoolean("AllowWyvernUpgrader", false);
			String[] listPetRentNpc = NPC.getString("ListPetRentNpc", "30827").split(",");
			LIST_PET_RENT_NPC = new ArrayList<>(listPetRentNpc.length);
			for (String id : listPetRentNpc)
			{
				LIST_PET_RENT_NPC.add(Integer.valueOf(id));
			}
			RAID_HP_REGEN_MULTIPLIER = NPC.getDouble("RaidHpRegenMultiplier", 100) / 100;
			RAID_MP_REGEN_MULTIPLIER = NPC.getDouble("RaidMpRegenMultiplier", 100) / 100;
			RAID_PDEFENCE_MULTIPLIER = NPC.getDouble("RaidPDefenceMultiplier", 100) / 100;
			RAID_MDEFENCE_MULTIPLIER = NPC.getDouble("RaidMDefenceMultiplier", 100) / 100;
			RAID_PATTACK_MULTIPLIER = NPC.getDouble("RaidPAttackMultiplier", 100) / 100;
			RAID_MATTACK_MULTIPLIER = NPC.getDouble("RaidMAttackMultiplier", 100) / 100;
			RAID_MIN_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMinRespawnMultiplier", 1.0f);
			RAID_MAX_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMaxRespawnMultiplier", 1.0f);
			RAID_MINION_RESPAWN_TIMER = NPC.getInt("RaidMinionRespawnTime", 300000);
			final String[] propertySplit = NPC.getString("CustomMinionsRespawnTime", "").split(";");
			MINIONS_RESPAWN_TIME = new HashMap<>(propertySplit.length);
			for (String prop : propertySplit)
			{
				String[] propSplit = prop.split(",");
				if (propSplit.length != 2)
				{
					_log.warning(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", prop, "\""));
				}
				
				try
				{
					MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (!prop.isEmpty())
					{
						_log.warning(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", propSplit[0], "\"", propSplit[1]));
					}
				}
			}
			
			RAID_DISABLE_CURSE = NPC.getBoolean("DisableRaidCurse", false);
			RAID_CHAOS_TIME = NPC.getInt("RaidChaosTime", 10);
			GRAND_CHAOS_TIME = NPC.getInt("GrandChaosTime", 10);
			MINION_CHAOS_TIME = NPC.getInt("MinionChaosTime", 10);
			INVENTORY_MAXIMUM_PET = NPC.getInt("MaximumSlotsForPet", 12);
			PET_HP_REGEN_MULTIPLIER = NPC.getDouble("PetHpRegenMultiplier", 100) / 100;
			PET_MP_REGEN_MULTIPLIER = NPC.getDouble("PetMpRegenMultiplier", 100) / 100;
			
			DROP_ADENA_MIN_LEVEL_DIFFERENCE = NPC.getInt("DropAdenaMinLevelDifference", 8);
			DROP_ADENA_MAX_LEVEL_DIFFERENCE = NPC.getInt("DropAdenaMaxLevelDifference", 15);
			DROP_ADENA_MIN_LEVEL_GAP_CHANCE = NPC.getDouble("DropAdenaMinLevelGapChance", 10);
			
			DROP_ITEM_MIN_LEVEL_DIFFERENCE = NPC.getInt("DropItemMinLevelDifference", 5);
			DROP_ITEM_MAX_LEVEL_DIFFERENCE = NPC.getInt("DropItemMaxLevelDifference", 10);
			DROP_ITEM_MIN_LEVEL_GAP_CHANCE = NPC.getDouble("DropItemMinLevelGapChance", 10);
			
			// Load Rates L2Properties file (if exists)
			final PropertiesParser RatesSettings = new PropertiesParser(RATES_CONFIG_FILE);
			
			RATE_XP = RatesSettings.getFloat("RateXp", 1);
			RATE_SP = RatesSettings.getFloat("RateSp", 1);
			RATE_PARTY_XP = RatesSettings.getFloat("RatePartyXp", 1);
			RATE_PARTY_SP = RatesSettings.getFloat("RatePartySp", 1);
			RATE_EXTRACTABLE = RatesSettings.getFloat("RateExtractable", 1);
			RATE_DROP_MANOR = RatesSettings.getInt("RateDropManor", 1);
			RATE_QUEST_DROP = RatesSettings.getFloat("RateQuestDrop", 1);
			RATE_QUEST_REWARD = RatesSettings.getFloat("RateQuestReward", 1);
			RATE_QUEST_REWARD_XP = RatesSettings.getFloat("RateQuestRewardXP", 1);
			RATE_QUEST_REWARD_SP = RatesSettings.getFloat("RateQuestRewardSP", 1);
			RATE_QUEST_REWARD_ADENA = RatesSettings.getFloat("RateQuestRewardAdena", 1);
			RATE_QUEST_REWARD_USE_MULTIPLIERS = RatesSettings.getBoolean("UseQuestRewardMultipliers", false);
			RATE_QUEST_REWARD_POTION = RatesSettings.getFloat("RateQuestRewardPotion", 1);
			RATE_QUEST_REWARD_SCROLL = RatesSettings.getFloat("RateQuestRewardScroll", 1);
			RATE_QUEST_REWARD_RECIPE = RatesSettings.getFloat("RateQuestRewardRecipe", 1);
			RATE_QUEST_REWARD_MATERIAL = RatesSettings.getFloat("RateQuestRewardMaterial", 1);
			RATE_HB_TRUST_INCREASE = RatesSettings.getFloat("RateHellboundTrustIncrease", 1);
			RATE_HB_TRUST_DECREASE = RatesSettings.getFloat("RateHellboundTrustDecrease", 1);
			
			RATE_VITALITY_LEVEL_1 = RatesSettings.getFloat("RateVitalityLevel1", 1.5f);
			RATE_VITALITY_LEVEL_2 = RatesSettings.getFloat("RateVitalityLevel2", 2);
			RATE_VITALITY_LEVEL_3 = RatesSettings.getFloat("RateVitalityLevel3", 2.5f);
			RATE_VITALITY_LEVEL_4 = RatesSettings.getFloat("RateVitalityLevel4", 3);
			RATE_RECOVERY_VITALITY_PEACE_ZONE = RatesSettings.getFloat("RateRecoveryPeaceZone", 1);
			RATE_VITALITY_LOST = RatesSettings.getFloat("RateVitalityLost", 1);
			RATE_VITALITY_GAIN = RatesSettings.getFloat("RateVitalityGain", 1);
			RATE_RECOVERY_ON_RECONNECT = RatesSettings.getFloat("RateRecoveryOnReconnect", 4);
			RATE_KARMA_LOST = RatesSettings.getFloat("RateKarmaLost", -1);
			if (RATE_KARMA_LOST == -1)
			{
				RATE_KARMA_LOST = RATE_XP;
			}
			RATE_KARMA_EXP_LOST = RatesSettings.getFloat("RateKarmaExpLost", 1);
			RATE_SIEGE_GUARDS_PRICE = RatesSettings.getFloat("RateSiegeGuardsPrice", 1);
			PLAYER_DROP_LIMIT = RatesSettings.getInt("PlayerDropLimit", 3);
			PLAYER_RATE_DROP = RatesSettings.getInt("PlayerRateDrop", 5);
			PLAYER_RATE_DROP_ITEM = RatesSettings.getInt("PlayerRateDropItem", 70);
			PLAYER_RATE_DROP_EQUIP = RatesSettings.getInt("PlayerRateDropEquip", 25);
			PLAYER_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("PlayerRateDropEquipWeapon", 5);
			PET_XP_RATE = RatesSettings.getFloat("PetXpRate", 1);
			PET_FOOD_RATE = RatesSettings.getInt("PetFoodRate", 1);
			SINEATER_XP_RATE = RatesSettings.getFloat("SinEaterXpRate", 1);
			KARMA_DROP_LIMIT = RatesSettings.getInt("KarmaDropLimit", 10);
			KARMA_RATE_DROP = RatesSettings.getInt("KarmaRateDrop", 70);
			KARMA_RATE_DROP_ITEM = RatesSettings.getInt("KarmaRateDropItem", 50);
			KARMA_RATE_DROP_EQUIP = RatesSettings.getInt("KarmaRateDropEquip", 40);
			KARMA_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("KarmaRateDropEquipWeapon", 10);
			
			RATE_DEATH_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("DeathDropAmountMultiplier", 1);
			RATE_CORPSE_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("CorpseDropAmountMultiplier", 1);
			RATE_HERB_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("HerbDropAmountMultiplier", 1);
			RATE_RAID_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("RaidDropAmountMultiplier", 1);
			RATE_DEATH_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("DeathDropChanceMultiplier", 1);
			RATE_CORPSE_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("CorpseDropChanceMultiplier", 1);
			RATE_HERB_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("HerbDropChanceMultiplier", 1);
			RATE_RAID_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("RaidDropChanceMultiplier", 1);
			String[] dropAmountMultiplier = RatesSettings.getString("DropAmountMultiplierByItemId", "").split(";");
			RATE_DROP_AMOUNT_MULTIPLIER = new HashMap<>(dropAmountMultiplier.length);
			if (!dropAmountMultiplier[0].isEmpty())
			{
				for (String item : dropAmountMultiplier)
				{
					String[] itemSplit = item.split(",");
					if (itemSplit.length != 2)
					{
						_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
					}
					else
					{
						try
						{
							RATE_DROP_AMOUNT_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!item.isEmpty())
							{
								_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
							}
						}
					}
				}
			}
			
			String[] dropChanceMultiplier = RatesSettings.getString("DropChanceMultiplierByItemId", "").split(";");
			RATE_DROP_CHANCE_MULTIPLIER = new HashMap<>(dropChanceMultiplier.length);
			if (!dropChanceMultiplier[0].isEmpty())
			{
				for (String item : dropChanceMultiplier)
				{
					String[] itemSplit = item.split(",");
					if (itemSplit.length != 2)
					{
						_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
					}
					else
					{
						try
						{
							RATE_DROP_CHANCE_MULTIPLIER.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!item.isEmpty())
							{
								_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
							}
						}
					}
				}
			}
			
			// Load L2JMod L2Properties file (if exists)
			final PropertiesParser L2JModSettings = new PropertiesParser(L2JMOD_CONFIG_FILE);
			
			L2JMOD_CHAMPION_ENABLE = L2JModSettings.getBoolean("ChampionEnable", false);
			L2JMOD_CHAMPION_PASSIVE = L2JModSettings.getBoolean("ChampionPassive", false);
			L2JMOD_CHAMPION_FREQUENCY = L2JModSettings.getInt("ChampionFrequency", 0);
			L2JMOD_CHAMP_TITLE = L2JModSettings.getString("ChampionTitle", "Champion");
			L2JMOD_CHAMP_MIN_LVL = L2JModSettings.getInt("ChampionMinLevel", 20);
			L2JMOD_CHAMP_MAX_LVL = L2JModSettings.getInt("ChampionMaxLevel", 60);
			L2JMOD_CHAMPION_HP = L2JModSettings.getInt("ChampionHp", 7);
			L2JMOD_CHAMPION_HP_REGEN = L2JModSettings.getFloat("ChampionHpRegen", 1);
			L2JMOD_CHAMPION_REWARDS = L2JModSettings.getInt("ChampionRewards", 8);
			L2JMOD_CHAMPION_ADENAS_REWARDS = L2JModSettings.getFloat("ChampionAdenasRewards", 1);
			L2JMOD_CHAMPION_ATK = L2JModSettings.getFloat("ChampionAtk", 1);
			L2JMOD_CHAMPION_SPD_ATK = L2JModSettings.getFloat("ChampionSpdAtk", 1);
			L2JMOD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = L2JModSettings.getInt("ChampionRewardLowerLvlItemChance", 0);
			L2JMOD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = L2JModSettings.getInt("ChampionRewardHigherLvlItemChance", 0);
			L2JMOD_CHAMPION_REWARD_ID = L2JModSettings.getInt("ChampionRewardItemID", 6393);
			L2JMOD_CHAMPION_REWARD_QTY = L2JModSettings.getInt("ChampionRewardItemQty", 1);
			L2JMOD_CHAMPION_ENABLE_VITALITY = L2JModSettings.getBoolean("ChampionEnableVitality", false);
			L2JMOD_CHAMPION_ENABLE_IN_INSTANCES = L2JModSettings.getBoolean("ChampionEnableInInstances", false);
			
			TVT_EVENT_ENABLED = L2JModSettings.getBoolean("TvTEventEnabled", false);
			TVT_EVENT_IN_INSTANCE = L2JModSettings.getBoolean("TvTEventInInstance", false);
			TVT_EVENT_INSTANCE_FILE = L2JModSettings.getString("TvTEventInstanceFile", "coliseum.xml");
			TVT_EVENT_INTERVAL = L2JModSettings.getString("TvTEventInterval", "20:00").split(",");
			TVT_EVENT_PARTICIPATION_TIME = L2JModSettings.getInt("TvTEventParticipationTime", 3600);
			TVT_EVENT_RUNNING_TIME = L2JModSettings.getInt("TvTEventRunningTime", 1800);
			TVT_EVENT_PARTICIPATION_NPC_ID = L2JModSettings.getInt("TvTEventParticipationNpcId", 0);
			
			L2JMOD_ALLOW_WEDDING = L2JModSettings.getBoolean("AllowWedding", false);
			L2JMOD_WEDDING_PRICE = L2JModSettings.getInt("WeddingPrice", 250000000);
			L2JMOD_WEDDING_PUNISH_INFIDELITY = L2JModSettings.getBoolean("WeddingPunishInfidelity", true);
			L2JMOD_WEDDING_TELEPORT = L2JModSettings.getBoolean("WeddingTeleport", true);
			L2JMOD_WEDDING_TELEPORT_PRICE = L2JModSettings.getInt("WeddingTeleportPrice", 50000);
			L2JMOD_WEDDING_TELEPORT_DURATION = L2JModSettings.getInt("WeddingTeleportDuration", 60);
			L2JMOD_WEDDING_SAMESEX = L2JModSettings.getBoolean("WeddingAllowSameSex", false);
			L2JMOD_WEDDING_FORMALWEAR = L2JModSettings.getBoolean("WeddingFormalWear", true);
			L2JMOD_WEDDING_DIVORCE_COSTS = L2JModSettings.getInt("WeddingDivorceCosts", 20);
			
			L2JMOD_ENABLE_WAREHOUSESORTING_CLAN = L2JModSettings.getBoolean("EnableWarehouseSortingClan", false);
			L2JMOD_ENABLE_WAREHOUSESORTING_PRIVATE = L2JModSettings.getBoolean("EnableWarehouseSortingPrivate", false);
			
			if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
			{
				TVT_EVENT_ENABLED = false;
				_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
			}
			else
			{
				String[] tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
				if (tvtNpcCoords.length < 3)
				{
					TVT_EVENT_ENABLED = false;
					_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
				}
				else
				{
					TVT_EVENT_REWARDS = new ArrayList<>();
					TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
					TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
					TVT_EVENT_TEAM_1_COORDINATES = new int[3];
					TVT_EVENT_TEAM_2_COORDINATES = new int[3];
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
					if (tvtNpcCoords.length == 4)
					{
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(tvtNpcCoords[3]);
					}
					TVT_EVENT_MIN_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMinPlayersInTeams", 1);
					TVT_EVENT_MAX_PLAYERS_IN_TEAMS = L2JModSettings.getInt("TvTEventMaxPlayersInTeams", 20);
					TVT_EVENT_MIN_LVL = L2JModSettings.getByte("TvTEventMinPlayerLevel", (byte) 1);
					TVT_EVENT_MAX_LVL = L2JModSettings.getByte("TvTEventMaxPlayerLevel", (byte) 80);
					TVT_EVENT_RESPAWN_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventRespawnTeleportDelay", 20);
					TVT_EVENT_START_LEAVE_TELEPORT_DELAY = L2JModSettings.getInt("TvTEventStartLeaveTeleportDelay", 20);
					TVT_EVENT_EFFECTS_REMOVAL = L2JModSettings.getInt("TvTEventEffectsRemoval", 0);
					TVT_EVENT_MAX_PARTICIPANTS_PER_IP = L2JModSettings.getInt("TvTEventMaxParticipantsPerIP", 0);
					TVT_ALLOW_VOICED_COMMAND = L2JModSettings.getBoolean("TvTAllowVoicedInfoCommand", false);
					TVT_EVENT_TEAM_1_NAME = L2JModSettings.getString("TvTEventTeam1Name", "Team1");
					tvtNpcCoords = L2JModSettings.getString("TvTEventTeam1Coordinates", "0,0,0").split(",");
					if (tvtNpcCoords.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
					}
					else
					{
						TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
						TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
						TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
						TVT_EVENT_TEAM_2_NAME = L2JModSettings.getString("TvTEventTeam2Name", "Team2");
						tvtNpcCoords = L2JModSettings.getString("TvTEventTeam2Coordinates", "0,0,0").split(",");
						if (tvtNpcCoords.length < 3)
						{
							TVT_EVENT_ENABLED = false;
							_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
						}
						else
						{
							TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
							TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
							TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
							tvtNpcCoords = L2JModSettings.getString("TvTEventParticipationFee", "0,0").split(",");
							try
							{
								TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(tvtNpcCoords[0]);
								TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(tvtNpcCoords[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (tvtNpcCoords.length > 0)
								{
									_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
								}
							}
							tvtNpcCoords = L2JModSettings.getString("TvTEventReward", "57,100000").split(";");
							for (String reward : tvtNpcCoords)
							{
								String[] rewardSplit = reward.split(",");
								if (rewardSplit.length != 2)
								{
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
								}
								else
								{
									try
									{
										TVT_EVENT_REWARDS.add(new int[]
										{
											Integer.parseInt(rewardSplit[0]),
											Integer.parseInt(rewardSplit[1])
										});
									}
									catch (NumberFormatException nfe)
									{
										if (!reward.isEmpty())
										{
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
										}
									}
								}
							}
							
							TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = L2JModSettings.getBoolean("TvTEventTargetTeamMembersAllowed", true);
							TVT_EVENT_SCROLL_ALLOWED = L2JModSettings.getBoolean("TvTEventScrollsAllowed", false);
							TVT_EVENT_POTIONS_ALLOWED = L2JModSettings.getBoolean("TvTEventPotionsAllowed", false);
							TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = L2JModSettings.getBoolean("TvTEventSummonByItemAllowed", false);
							TVT_REWARD_TEAM_TIE = L2JModSettings.getBoolean("TvTRewardTeamTie", false);
							tvtNpcCoords = L2JModSettings.getString("TvTDoorsToOpen", "").split(";");
							for (String door : tvtNpcCoords)
							{
								try
								{
									TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
								}
								catch (NumberFormatException nfe)
								{
									if (!door.isEmpty())
									{
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen \"", door, "\""));
									}
								}
							}
							
							tvtNpcCoords = L2JModSettings.getString("TvTDoorsToClose", "").split(";");
							for (String door : tvtNpcCoords)
							{
								try
								{
									TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
								}
								catch (NumberFormatException nfe)
								{
									if (!door.isEmpty())
									{
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose \"", door, "\""));
									}
								}
							}
							
							tvtNpcCoords = L2JModSettings.getString("TvTEventFighterBuffs", "").split(";");
							if (!tvtNpcCoords[0].isEmpty())
							{
								TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(tvtNpcCoords.length);
								for (String skill : tvtNpcCoords)
								{
									String[] skillSplit = skill.split(",");
									if (skillSplit.length != 2)
									{
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
									}
									else
									{
										try
										{
											TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
										}
										catch (NumberFormatException nfe)
										{
											if (!skill.isEmpty())
											{
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
											}
										}
									}
								}
							}
							
							tvtNpcCoords = L2JModSettings.getString("TvTEventMageBuffs", "").split(";");
							if (!tvtNpcCoords[0].isEmpty())
							{
								TVT_EVENT_MAGE_BUFFS = new HashMap<>(tvtNpcCoords.length);
								for (String skill : tvtNpcCoords)
								{
									String[] skillSplit = skill.split(",");
									if (skillSplit.length != 2)
									{
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
									}
									else
									{
										try
										{
											TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
										}
										catch (NumberFormatException nfe)
										{
											if (!skill.isEmpty())
											{
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			BANKING_SYSTEM_ENABLED = L2JModSettings.getBoolean("BankingEnabled", false);
			BANKING_SYSTEM_GOLDBARS = L2JModSettings.getInt("BankingGoldbarCount", 1);
			BANKING_SYSTEM_ADENA = L2JModSettings.getInt("BankingAdenaCount", 500000000);
			
			OFFLINE_TRADE_ENABLE = L2JModSettings.getBoolean("OfflineTradeEnable", false);
			OFFLINE_CRAFT_ENABLE = L2JModSettings.getBoolean("OfflineCraftEnable", false);
			OFFLINE_MODE_IN_PEACE_ZONE = L2JModSettings.getBoolean("OfflineModeInPeaceZone", false);
			OFFLINE_MODE_NO_DAMAGE = L2JModSettings.getBoolean("OfflineModeNoDamage", false);
			OFFLINE_SET_NAME_COLOR = L2JModSettings.getBoolean("OfflineSetNameColor", false);
			OFFLINE_NAME_COLOR = Integer.decode("0x" + L2JModSettings.getString("OfflineNameColor", "808080"));
			OFFLINE_FAME = L2JModSettings.getBoolean("OfflineFame", true);
			RESTORE_OFFLINERS = L2JModSettings.getBoolean("RestoreOffliners", false);
			OFFLINE_MAX_DAYS = L2JModSettings.getInt("OfflineMaxDays", 10);
			OFFLINE_DISCONNECT_FINISHED = L2JModSettings.getBoolean("OfflineDisconnectFinished", true);
			
			L2JMOD_ENABLE_MANA_POTIONS_SUPPORT = L2JModSettings.getBoolean("EnableManaPotionSupport", false);
			
			L2JMOD_DISPLAY_SERVER_TIME = L2JModSettings.getBoolean("DisplayServerTime", false);
			
			WELCOME_MESSAGE_ENABLED = L2JModSettings.getBoolean("ScreenWelcomeMessageEnable", false);
			WELCOME_MESSAGE_TEXT = L2JModSettings.getString("ScreenWelcomeMessageText", "Welcome to L2J server!");
			WELCOME_MESSAGE_TIME = L2JModSettings.getInt("ScreenWelcomeMessageTime", 10) * 1000;
			
			L2JMOD_ANTIFEED_ENABLE = L2JModSettings.getBoolean("AntiFeedEnable", false);
			L2JMOD_ANTIFEED_DUALBOX = L2JModSettings.getBoolean("AntiFeedDualbox", true);
			L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX = L2JModSettings.getBoolean("AntiFeedDisconnectedAsDualbox", true);
			L2JMOD_ANTIFEED_INTERVAL = L2JModSettings.getInt("AntiFeedInterval", 120) * 1000;
			ANNOUNCE_PK_PVP = L2JModSettings.getBoolean("AnnouncePkPvP", false);
			ANNOUNCE_PK_PVP_NORMAL_MESSAGE = L2JModSettings.getBoolean("AnnouncePkPvPNormalMessage", true);
			ANNOUNCE_PK_MSG = L2JModSettings.getString("AnnouncePkMsg", "$killer has slaughtered $target");
			ANNOUNCE_PVP_MSG = L2JModSettings.getString("AnnouncePvpMsg", "$killer has defeated $target");
			
			L2JMOD_CHAT_ADMIN = L2JModSettings.getBoolean("ChatAdmin", false);
			
			L2JMOD_MULTILANG_DEFAULT = L2JModSettings.getString("MultiLangDefault", "en");
			L2JMOD_MULTILANG_ENABLE = L2JModSettings.getBoolean("MultiLangEnable", false);
			String[] allowed = L2JModSettings.getString("MultiLangAllowed", L2JMOD_MULTILANG_DEFAULT).split(";");
			L2JMOD_MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				L2JMOD_MULTILANG_ALLOWED.add(lang);
			}
			
			if (!L2JMOD_MULTILANG_ALLOWED.contains(L2JMOD_MULTILANG_DEFAULT))
			{
				_log.warning("MultiLang[Config.load()]: default language: " + L2JMOD_MULTILANG_DEFAULT + " is not in allowed list !");
			}
			
			L2JMOD_HELLBOUND_STATUS = L2JModSettings.getBoolean("HellboundStatus", false);
			L2JMOD_MULTILANG_VOICED_ALLOW = L2JModSettings.getBoolean("MultiLangVoiceCommand", true);
			L2JMOD_MULTILANG_SM_ENABLE = L2JModSettings.getBoolean("MultiLangSystemMessageEnable", false);
			allowed = L2JModSettings.getString("MultiLangSystemMessageAllowed", "").split(";");
			L2JMOD_MULTILANG_SM_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				if (!lang.isEmpty())
				{
					L2JMOD_MULTILANG_SM_ALLOWED.add(lang);
				}
			}
			L2JMOD_MULTILANG_NS_ENABLE = L2JModSettings.getBoolean("MultiLangNpcStringEnable", false);
			allowed = L2JModSettings.getString("MultiLangNpcStringAllowed", "").split(";");
			L2JMOD_MULTILANG_NS_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				if (!lang.isEmpty())
				{
					L2JMOD_MULTILANG_NS_ALLOWED.add(lang);
				}
			}
			
			L2WALKER_PROTECTION = L2JModSettings.getBoolean("L2WalkerProtection", false);
			L2JMOD_DEBUG_VOICE_COMMAND = L2JModSettings.getBoolean("DebugVoiceCommand", false);
			
			L2JMOD_DUALBOX_CHECK_MAX_PLAYERS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxPlayersPerIP", 0);
			L2JMOD_DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxOlympiadParticipantsPerIP", 0);
			L2JMOD_DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = L2JModSettings.getInt("DualboxCheckMaxL2EventParticipantsPerIP", 0);
			String[] dualboxCheckWhiteList = L2JModSettings.getString("DualboxCheckWhitelist", "127.0.0.1,0").split(";");
			L2JMOD_DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
			for (String entry : dualboxCheckWhiteList)
			{
				String[] entrySplit = entry.split(",");
				if (entrySplit.length != 2)
				{
					_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist \"", entry, "\""));
				}
				else
				{
					try
					{
						int num = Integer.parseInt(entrySplit[1]);
						num = num == 0 ? -1 : num;
						L2JMOD_DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
					}
					catch (UnknownHostException e)
					{
						_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist \"", entrySplit[0], "\""));
					}
					catch (NumberFormatException e)
					{
						_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist \"", entrySplit[1], "\""));
					}
				}
			}
			L2JMOD_ALLOW_CHANGE_PASSWORD = L2JModSettings.getBoolean("AllowChangePassword", false);
			
			// Load PvP L2Properties file (if exists)
			final PropertiesParser PVPSettings = new PropertiesParser(PVP_CONFIG_FILE);
			
			KARMA_DROP_GM = PVPSettings.getBoolean("CanGMDropEquipment", false);
			KARMA_AWARD_PK_KILL = PVPSettings.getBoolean("AwardPKKillPVPPoint", false);
			KARMA_PK_LIMIT = PVPSettings.getInt("MinimumPKRequiredToDrop", 5);
			KARMA_NONDROPPABLE_PET_ITEMS = PVPSettings.getString("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650,9882");
			KARMA_NONDROPPABLE_ITEMS = PVPSettings.getString("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,7694,8181,5575,7694,9388,9389,9390");
			
			String[] karma = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
			KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[karma.length];
			
			for (int i = 0; i < karma.length; i++)
			{
				KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(karma[i]);
			}
			
			karma = KARMA_NONDROPPABLE_ITEMS.split(",");
			KARMA_LIST_NONDROPPABLE_ITEMS = new int[karma.length];
			
			for (int i = 0; i < karma.length; i++)
			{
				KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(karma[i]);
			}
			
			// sorting so binarySearch can be used later
			Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
			Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);
			
			PVP_NORMAL_TIME = PVPSettings.getInt("PvPVsNormalTime", 120000);
			PVP_PVP_TIME = PVPSettings.getInt("PvPVsPvPTime", 60000);
			
			// Load Olympiad L2Properties file (if exists)
			final PropertiesParser Olympiad = new PropertiesParser(OLYMPIAD_CONFIG_FILE);
			
			ALT_OLY_START_TIME = Olympiad.getInt("AltOlyStartTime", 18);
			ALT_OLY_MIN = Olympiad.getInt("AltOlyMin", 0);
			ALT_OLY_MAX_BUFFS = Olympiad.getInt("AltOlyMaxBuffs", 5);
			ALT_OLY_CPERIOD = Olympiad.getLong("AltOlyCPeriod", 21600000);
			ALT_OLY_BATTLE = Olympiad.getLong("AltOlyBattle", 300000);
			ALT_OLY_WPERIOD = Olympiad.getLong("AltOlyWPeriod", 604800000);
			ALT_OLY_VPERIOD = Olympiad.getLong("AltOlyVPeriod", 86400000);
			ALT_OLY_START_POINTS = Olympiad.getInt("AltOlyStartPoints", 10);
			ALT_OLY_WEEKLY_POINTS = Olympiad.getInt("AltOlyWeeklyPoints", 10);
			ALT_OLY_CLASSED = Olympiad.getInt("AltOlyClassedParticipants", 11);
			ALT_OLY_NONCLASSED = Olympiad.getInt("AltOlyNonClassedParticipants", 11);
			ALT_OLY_TEAMS = Olympiad.getInt("AltOlyTeamsParticipants", 6);
			ALT_OLY_REG_DISPLAY = Olympiad.getInt("AltOlyRegistrationDisplayNumber", 100);
			ALT_OLY_CLASSED_REWARD = parseItemsList(Olympiad.getString("AltOlyClassedReward", "13722,50"));
			ALT_OLY_NONCLASSED_REWARD = parseItemsList(Olympiad.getString("AltOlyNonClassedReward", "13722,40"));
			ALT_OLY_TEAM_REWARD = parseItemsList(Olympiad.getString("AltOlyTeamReward", "13722,85"));
			ALT_OLY_COMP_RITEM = Olympiad.getInt("AltOlyCompRewItem", 13722);
			ALT_OLY_MIN_MATCHES = Olympiad.getInt("AltOlyMinMatchesForPoints", 15);
			ALT_OLY_GP_PER_POINT = Olympiad.getInt("AltOlyGPPerPoint", 1000);
			ALT_OLY_HERO_POINTS = Olympiad.getInt("AltOlyHeroPoints", 200);
			ALT_OLY_RANK1_POINTS = Olympiad.getInt("AltOlyRank1Points", 100);
			ALT_OLY_RANK2_POINTS = Olympiad.getInt("AltOlyRank2Points", 75);
			ALT_OLY_RANK3_POINTS = Olympiad.getInt("AltOlyRank3Points", 55);
			ALT_OLY_RANK4_POINTS = Olympiad.getInt("AltOlyRank4Points", 40);
			ALT_OLY_RANK5_POINTS = Olympiad.getInt("AltOlyRank5Points", 30);
			ALT_OLY_MAX_POINTS = Olympiad.getInt("AltOlyMaxPoints", 10);
			ALT_OLY_DIVIDER_CLASSED = Olympiad.getInt("AltOlyDividerClassed", 5);
			ALT_OLY_DIVIDER_NON_CLASSED = Olympiad.getInt("AltOlyDividerNonClassed", 5);
			ALT_OLY_MAX_WEEKLY_MATCHES = Olympiad.getInt("AltOlyMaxWeeklyMatches", 70);
			ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED = Olympiad.getInt("AltOlyMaxWeeklyMatchesNonClassed", 60);
			ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED = Olympiad.getInt("AltOlyMaxWeeklyMatchesClassed", 30);
			ALT_OLY_MAX_WEEKLY_MATCHES_TEAM = Olympiad.getInt("AltOlyMaxWeeklyMatchesTeam", 10);
			ALT_OLY_LOG_FIGHTS = Olympiad.getBoolean("AltOlyLogFights", false);
			ALT_OLY_SHOW_MONTHLY_WINNERS = Olympiad.getBoolean("AltOlyShowMonthlyWinners", true);
			ALT_OLY_ANNOUNCE_GAMES = Olympiad.getBoolean("AltOlyAnnounceGames", true);
			String[] olyRestrictedItems = Olympiad.getString("AltOlyRestrictedItems", "6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,9388,9389,9390,17049,17050,17051,17052,17053,17054,17055,17056,17057,17058,17059,17060,17061,20759,20775,20776,20777,20778,14774").split(",");
			LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>(olyRestrictedItems.length);
			for (String id : olyRestrictedItems)
			{
				LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
			}
			ALT_OLY_ENCHANT_LIMIT = Olympiad.getInt("AltOlyEnchantLimit", -1);
			ALT_OLY_WAIT_TIME = Olympiad.getInt("AltOlyWaitTime", 120);
			
			final File hexIdFile = new File(HEXID_FILE);
			if (hexIdFile.exists())
			{
				final PropertiesParser hexId = new PropertiesParser(hexIdFile);
				
				if (hexId.containskey("ServerID") && hexId.containskey("HexID"))
				{
					SERVER_ID = hexId.getInt("ServerID", 1);
					try
					{
						HEX_ID = new BigInteger(hexId.getString("HexID", null), 16).toByteArray();
					}
					catch (Exception e)
					{
						_log.warning("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
					}
				}
				else
				{
					_log.warning("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
				}
			}
			else
			{
				_log.warning("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
			}
			
			// Grand bosses
			final PropertiesParser GrandBossSettings = new PropertiesParser(GRANDBOSS_CONFIG_FILE);
			
			ANTHARAS_WAIT_TIME = GrandBossSettings.getInt("AntharasWaitTime", 30);
			ANTHARAS_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfAntharasSpawn", 264);
			ANTHARAS_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfAntharasSpawn", 72);
			
			VALAKAS_WAIT_TIME = GrandBossSettings.getInt("ValakasWaitTime", 30);
			VALAKAS_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfValakasSpawn", 264);
			VALAKAS_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfValakasSpawn", 72);
			
			BAIUM_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfBaiumSpawn", 168);
			BAIUM_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfBaiumSpawn", 48);
			
			CORE_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfCoreSpawn", 60);
			CORE_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfCoreSpawn", 24);
			
			ORFEN_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfOrfenSpawn", 48);
			ORFEN_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfOrfenSpawn", 20);
			
			QUEEN_ANT_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfQueenAntSpawn", 36);
			QUEEN_ANT_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfQueenAntSpawn", 17);
			
			BELETH_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfBelethSpawn", 192);
			BELETH_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfBelethSpawn", 148);
			BELETH_MIN_PLAYERS = GrandBossSettings.getInt("BelethMinPlayers", 36);
			
			// Gracia Seeds
			final PropertiesParser GraciaSeedsSettings = new PropertiesParser(GRACIASEEDS_CONFIG_FILE);
			
			// Seed of Destruction
			SOD_TIAT_KILL_COUNT = GraciaSeedsSettings.getInt("TiatKillCountForNextState", 10);
			SOD_STAGE_2_LENGTH = GraciaSeedsSettings.getLong("Stage2Length", 720) * 60000;
			
			try
			{
				//@formatter:off
				FILTER_LIST = Files.lines(Paths.get(CHAT_FILTER_FILE), StandardCharsets.UTF_8)
					.map(String::trim)
					.filter(line -> (!line.isEmpty() && (line.charAt(0) != '#')))
					.collect(Collectors.toList());
				//@formatter:on
				_log.info("Loaded " + FILTER_LIST.size() + " Filter Words.");
			}
			catch (IOException ioe)
			{
				_log.log(Level.WARNING, "Error while loading chat filter words!", ioe);
			}
			
			final PropertiesParser ClanHallSiege = new PropertiesParser(CH_SIEGE_FILE);
			
			CHS_MAX_ATTACKERS = ClanHallSiege.getInt("MaxAttackers", 500);
			CHS_CLAN_MINLEVEL = ClanHallSiege.getInt("MinClanLevel", 4);
			CHS_MAX_FLAGS_PER_CLAN = ClanHallSiege.getInt("MaxFlagsPerClan", 1);
			CHS_ENABLE_FAME = ClanHallSiege.getBoolean("EnableFame", false);
			CHS_FAME_AMOUNT = ClanHallSiege.getInt("FameAmount", 0);
			CHS_FAME_FREQUENCY = ClanHallSiege.getInt("FameFrequency", 0);
			
			final PropertiesParser geoData = new PropertiesParser(GEODATA_FILE);
			
			try
			{
				PATHNODE_DIR = new File(geoData.getString("PathnodeDirectory", "data/pathnode").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, "Error setting pathnode directory!", e);
				PATHNODE_DIR = new File("data/pathnode");
			}
			
			PATHFINDING = geoData.getInt("PathFinding", 0);
			PATHFIND_BUFFERS = geoData.getString("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
			LOW_WEIGHT = geoData.getFloat("LowWeight", 0.5f);
			MEDIUM_WEIGHT = geoData.getFloat("MediumWeight", 2);
			HIGH_WEIGHT = geoData.getFloat("HighWeight", 3);
			ADVANCED_DIAGONAL_STRATEGY = geoData.getBoolean("AdvancedDiagonalStrategy", true);
			DIAGONAL_WEIGHT = geoData.getFloat("DiagonalWeight", 0.707f);
			MAX_POSTFILTER_PASSES = geoData.getInt("MaxPostfilterPasses", 3);
			DEBUG_PATH = geoData.getBoolean("DebugPath", false);
			FORCE_GEODATA = geoData.getBoolean("ForceGeoData", true);
			COORD_SYNCHRONIZE = geoData.getInt("CoordSynchronize", -1);
			GEODATA_PATH = Paths.get(geoData.getString("GeoDataPath", "./data/geodata"));
			TRY_LOAD_UNSPECIFIED_REGIONS = geoData.getBoolean("TryLoadUnspecifiedRegions", true);
			GEODATA_REGIONS = new HashMap<>();
			for (int regionX = L2World.TILE_X_MIN; regionX <= L2World.TILE_X_MAX; regionX++)
			{
				for (int regionY = L2World.TILE_Y_MIN; regionY <= L2World.TILE_Y_MAX; regionY++)
				{
					String key = regionX + "_" + regionY;
					if (geoData.containskey(regionX + "_" + regionY))
					{
						GEODATA_REGIONS.put(key, geoData.getBoolean(key, false));
					}
				}
			}
		}
		else if (Server.serverMode == Server.MODE_LOGINSERVER)
		{
			final PropertiesParser ServerSettings = new PropertiesParser(LOGIN_CONFIGURATION_FILE);
			
			ENABLE_UPNP = ServerSettings.getBoolean("EnableUPnP", true);
			GAME_SERVER_LOGIN_HOST = ServerSettings.getString("LoginHostname", "127.0.0.1");
			GAME_SERVER_LOGIN_PORT = ServerSettings.getInt("LoginPort", 9013);
			
			LOGIN_BIND_ADDRESS = ServerSettings.getString("LoginserverHostname", "*");
			PORT_LOGIN = ServerSettings.getInt("LoginserverPort", 2106);
			
			try
			{
				DATAPACK_ROOT = new File(ServerSettings.getString("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, "Error setting datapack root!", e);
				DATAPACK_ROOT = new File(".");
			}
			
			DEBUG = ServerSettings.getBoolean("Debug", false);
			
			ACCEPT_NEW_GAMESERVER = ServerSettings.getBoolean("AcceptNewGameServer", true);
			
			LOGIN_TRY_BEFORE_BAN = ServerSettings.getInt("LoginTryBeforeBan", 5);
			LOGIN_BLOCK_AFTER_BAN = ServerSettings.getInt("LoginBlockAfterBan", 900);
			
			LOGIN_SERVER_SCHEDULE_RESTART = ServerSettings.getBoolean("LoginRestartSchedule", false);
			LOGIN_SERVER_SCHEDULE_RESTART_TIME = ServerSettings.getLong("LoginRestartTime", 24);
			
			DATABASE_DRIVER = ServerSettings.getString("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL = ServerSettings.getString("URL", "jdbc:mysql://localhost/l2jls");
			DATABASE_LOGIN = ServerSettings.getString("Login", "root");
			DATABASE_PASSWORD = ServerSettings.getString("Password", "");
			DATABASE_MAX_CONNECTIONS = ServerSettings.getInt("MaximumDbConnections", 10);
			DATABASE_MAX_IDLE_TIME = ServerSettings.getInt("MaximumDbIdleTime", 0);
			CONNECTION_CLOSE_TIME = ServerSettings.getLong("ConnectionCloseTime", 60000);
			
			SHOW_LICENCE = ServerSettings.getBoolean("ShowLicence", true);
			
			AUTO_CREATE_ACCOUNTS = ServerSettings.getBoolean("AutoCreateAccounts", true);
			
			FLOOD_PROTECTION = ServerSettings.getBoolean("EnableFloodProtection", true);
			FAST_CONNECTION_LIMIT = ServerSettings.getInt("FastConnectionLimit", 15);
			NORMAL_CONNECTION_TIME = ServerSettings.getInt("NormalConnectionTime", 700);
			FAST_CONNECTION_TIME = ServerSettings.getInt("FastConnectionTime", 350);
			MAX_CONNECTION_PER_IP = ServerSettings.getInt("MaxConnectionPerIP", 50);
			
			// MMO
			final PropertiesParser mmoSettings = new PropertiesParser(MMO_CONFIG_FILE);
			
			MMO_SELECTOR_SLEEP_TIME = mmoSettings.getInt("SleepTime", 20);
			MMO_MAX_SEND_PER_PASS = mmoSettings.getInt("MaxSendPerPass", 12);
			MMO_MAX_READ_PER_PASS = mmoSettings.getInt("MaxReadPerPass", 12);
			MMO_HELPER_BUFFER_COUNT = mmoSettings.getInt("HelperBufferCount", 20);
			MMO_TCP_NODELAY = mmoSettings.getBoolean("TcpNoDelay", false);
			
			// Load Telnet L2Properties file (if exists)
			final PropertiesParser telnetSettings = new PropertiesParser(TELNET_FILE);
			
			IS_TELNET_ENABLED = telnetSettings.getBoolean("EnableTelnet", false);
			
			// Email
			final PropertiesParser emailSettings = new PropertiesParser(EMAIL_CONFIG_FILE);
			
			EMAIL_SERVERINFO_NAME = emailSettings.getString("ServerInfoName", "Unconfigured L2J Server");
			EMAIL_SERVERINFO_ADDRESS = emailSettings.getString("ServerInfoAddress", "info@myl2jserver.com");
			
			EMAIL_SYS_ENABLED = emailSettings.getBoolean("EmailSystemEnabled", false);
			EMAIL_SYS_HOST = emailSettings.getString("SmtpServerHost", "smtp.gmail.com");
			EMAIL_SYS_PORT = emailSettings.getInt("SmtpServerPort", 465);
			EMAIL_SYS_SMTP_AUTH = emailSettings.getBoolean("SmtpAuthRequired", true);
			EMAIL_SYS_FACTORY = emailSettings.getString("SmtpFactory", "javax.net.ssl.SSLSocketFactory");
			EMAIL_SYS_FACTORY_CALLBACK = emailSettings.getBoolean("SmtpFactoryCallback", false);
			EMAIL_SYS_USERNAME = emailSettings.getString("SmtpUsername", "user@gmail.com");
			EMAIL_SYS_PASSWORD = emailSettings.getString("SmtpPassword", "password");
			EMAIL_SYS_ADDRESS = emailSettings.getString("EmailSystemAddress", "noreply@myl2jserver.com");
			EMAIL_SYS_SELECTQUERY = emailSettings.getString("EmailDBSelectQuery", "SELECT value FROM account_data WHERE account_name=? AND var='email_addr'");
			EMAIL_SYS_DBFIELD = emailSettings.getString("EmailDBField", "value");
		}
		else
		{
			_log.severe("Could not Load Config: server mode was not set!");
		}
	}
	
	/**
	 * Set a new value to a config parameter.
	 * @param pName the name of the parameter whose value to change
	 * @param pValue the new value of the parameter
	 * @return {@code true} if the value of the parameter was changed, {@code false} otherwise
	 */
	public static boolean setParameterValue(String pName, String pValue)
	{
		switch (pName.trim().toLowerCase())
		{
		// rates.properties
			case "ratexp":
				RATE_XP = Float.parseFloat(pValue);
				break;
			case "ratesp":
				RATE_SP = Float.parseFloat(pValue);
				break;
			case "ratepartyxp":
				RATE_PARTY_XP = Float.parseFloat(pValue);
				break;
			case "rateextractable":
				RATE_EXTRACTABLE = Float.parseFloat(pValue);
				break;
			case "ratedropadena":
				RATE_DROP_AMOUNT_MULTIPLIER.put(Inventory.ADENA_ID, Float.parseFloat(pValue));
				break;
			case "ratedropmanor":
				RATE_DROP_MANOR = Integer.parseInt(pValue);
				break;
			case "ratequestdrop":
				RATE_QUEST_DROP = Float.parseFloat(pValue);
				break;
			case "ratequestreward":
				RATE_QUEST_REWARD = Float.parseFloat(pValue);
				break;
			case "ratequestrewardxp":
				RATE_QUEST_REWARD_XP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardsp":
				RATE_QUEST_REWARD_SP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardadena":
				RATE_QUEST_REWARD_ADENA = Float.parseFloat(pValue);
				break;
			case "usequestrewardmultipliers":
				RATE_QUEST_REWARD_USE_MULTIPLIERS = Boolean.parseBoolean(pValue);
				break;
			case "ratequestrewardpotion":
				RATE_QUEST_REWARD_POTION = Float.parseFloat(pValue);
				break;
			case "ratequestrewardscroll":
				RATE_QUEST_REWARD_SCROLL = Float.parseFloat(pValue);
				break;
			case "ratequestrewardrecipe":
				RATE_QUEST_REWARD_RECIPE = Float.parseFloat(pValue);
				break;
			case "ratequestrewardmaterial":
				RATE_QUEST_REWARD_MATERIAL = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustincrease":
				RATE_HB_TRUST_INCREASE = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustdecrease":
				RATE_HB_TRUST_DECREASE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel1":
				RATE_VITALITY_LEVEL_1 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel2":
				RATE_VITALITY_LEVEL_2 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel3":
				RATE_VITALITY_LEVEL_3 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel4":
				RATE_VITALITY_LEVEL_4 = Float.parseFloat(pValue);
				break;
			case "raterecoverypeacezone":
				RATE_RECOVERY_VITALITY_PEACE_ZONE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylost":
				RATE_VITALITY_LOST = Float.parseFloat(pValue);
				break;
			case "ratevitalitygain":
				RATE_VITALITY_GAIN = Float.parseFloat(pValue);
				break;
			case "raterecoveryonreconnect":
				RATE_RECOVERY_ON_RECONNECT = Float.parseFloat(pValue);
				break;
			case "ratekarmaexplost":
				RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
				break;
			case "ratesiegeguardsprice":
				RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
				break;
			case "ratecommonherbs":
				RATE_DROP_COMMON_HERBS = Float.parseFloat(pValue);
				break;
			case "ratehpherbs":
				RATE_DROP_HP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratempherbs":
				RATE_DROP_MP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratespecialherbs":
				RATE_DROP_SPECIAL_HERBS = Float.parseFloat(pValue);
				break;
			case "ratevitalityherbs":
				RATE_DROP_VITALITY_HERBS = Float.parseFloat(pValue);
				break;
			case "playerdroplimit":
				PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "playerratedrop":
				PLAYER_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "playerratedropitem":
				PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "playerratedropequip":
				PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "playerratedropequipweapon":
				PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "petxprate":
				PET_XP_RATE = Float.parseFloat(pValue);
				break;
			case "petfoodrate":
				PET_FOOD_RATE = Integer.parseInt(pValue);
				break;
			case "sineaterxprate":
				SINEATER_XP_RATE = Float.parseFloat(pValue);
				break;
			case "karmadroplimit":
				KARMA_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "karmaratedrop":
				KARMA_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "karmaratedropitem":
				KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "karmaratedropequip":
				KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "karmaratedropequipweapon":
				KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "autodestroydroppeditemafter":
				AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
				break;
			case "destroyplayerdroppeditem":
				DESTROY_DROPPED_PLAYER_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "destroyequipableitem":
				DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "savedroppeditem":
				SAVE_DROPPED_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "emptydroppeditemtableafterload":
				EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.parseBoolean(pValue);
				break;
			case "savedroppediteminterval":
				SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(pValue);
				break;
			case "cleardroppeditemtable":
				CLEAR_DROPPED_ITEM_TABLE = Boolean.parseBoolean(pValue);
				break;
			case "precisedropcalculation":
				PRECISE_DROP_CALCULATION = Boolean.parseBoolean(pValue);
				break;
			case "multipleitemdrop":
				MULTIPLE_ITEM_DROP = Boolean.parseBoolean(pValue);
				break;
			case "lowweight":
				LOW_WEIGHT = Float.parseFloat(pValue);
				break;
			case "mediumweight":
				MEDIUM_WEIGHT = Float.parseFloat(pValue);
				break;
			case "highweight":
				HIGH_WEIGHT = Float.parseFloat(pValue);
				break;
			case "advanceddiagonalstrategy":
				ADVANCED_DIAGONAL_STRATEGY = Boolean.parseBoolean(pValue);
				break;
			case "diagonalweight":
				DIAGONAL_WEIGHT = Float.parseFloat(pValue);
				break;
			case "maxpostfilterpasses":
				MAX_POSTFILTER_PASSES = Integer.parseInt(pValue);
				break;
			case "coordsynchronize":
				COORD_SYNCHRONIZE = Integer.parseInt(pValue);
				break;
			case "deletecharafterdays":
				DELETE_DAYS = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuesize":
				CLIENT_PACKET_QUEUE_SIZE = Integer.parseInt(pValue);
				if (CLIENT_PACKET_QUEUE_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 1;
				}
				break;
			case "clientpacketqueuemaxburstsize":
				CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = Integer.parseInt(pValue);
				if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS;
				}
				break;
			case "clientpacketqueuemaxpacketspersecond":
				CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemeasureinterval":
				CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxaveragepacketspersecond":
				CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxfloodspermin":
				CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxoverflowspermin":
				CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxunderflowspermin":
				CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxunknownpermin":
				CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = Integer.parseInt(pValue);
				break;
			case "allowdiscarditem":
				ALLOW_DISCARDITEM = Boolean.parseBoolean(pValue);
				break;
			case "allowrefund":
				ALLOW_REFUND = Boolean.parseBoolean(pValue);
				break;
			case "allowwarehouse":
				ALLOW_WAREHOUSE = Boolean.parseBoolean(pValue);
				break;
			case "allowwear":
				ALLOW_WEAR = Boolean.parseBoolean(pValue);
				break;
			case "weardelay":
				WEAR_DELAY = Integer.parseInt(pValue);
				break;
			case "wearprice":
				WEAR_PRICE = Integer.parseInt(pValue);
				break;
			case "defaultfinishtime":
				INSTANCE_FINISH_TIME = Integer.parseInt(pValue);
				break;
			case "restoreplayerinstance":
				RESTORE_PLAYER_INSTANCE = Boolean.parseBoolean(pValue);
				break;
			case "allowsummonininstance":
				ALLOW_SUMMON_IN_INSTANCE = Boolean.parseBoolean(pValue);
				break;
			case "ejectdeadplayertime":
				EJECT_DEAD_PLAYER_TIME = Integer.parseInt(pValue);
				break;
			case "allowwater":
				ALLOW_WATER = Boolean.parseBoolean(pValue);
				break;
			case "allowrentpet":
				ALLOW_RENTPET = Boolean.parseBoolean(pValue);
				break;
			case "boatbroadcastradius":
				BOAT_BROADCAST_RADIUS = Integer.parseInt(pValue);
				break;
			case "allowcursedweapons":
				ALLOW_CURSED_WEAPONS = Boolean.parseBoolean(pValue);
				break;
			case "allowmanor":
				ALLOW_MANOR = Boolean.parseBoolean(pValue);
				break;
			case "allowpetwalkers":
				ALLOW_PET_WALKERS = Boolean.parseBoolean(pValue);
				break;
			case "enablecommunityboard":
				ENABLE_COMMUNITY_BOARD = Boolean.parseBoolean(pValue);
				break;
			case "bbsdefault":
				BBS_DEFAULT = pValue;
				break;
			case "showservernews":
				SERVER_NEWS = Boolean.parseBoolean(pValue);
				break;
			case "shownpclevel":
				SHOW_NPC_LVL = Boolean.parseBoolean(pValue);
				break;
			case "showcrestwithoutquest":
				SHOW_CREST_WITHOUT_QUEST = Boolean.parseBoolean(pValue);
				break;
			case "forceinventoryupdate":
				FORCE_INVENTORY_UPDATE = Boolean.parseBoolean(pValue);
				break;
			case "autodeleteinvalidquestdata":
				AUTODELETE_INVALID_QUEST_DATA = Boolean.parseBoolean(pValue);
				break;
			case "maximumonlineusers":
				MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);
				break;
			case "peacezonemode":
				PEACE_ZONE_MODE = Integer.parseInt(pValue);
				break;
			case "checkknownlist":
				CHECK_KNOWN = Boolean.parseBoolean(pValue);
				break;
			case "maxdriftrange":
				MAX_DRIFT_RANGE = Integer.parseInt(pValue);
				break;
			case "usedeepbluedroprules":
				DEEPBLUE_DROP_RULES = Boolean.parseBoolean(pValue);
				break;
			case "usedeepbluedroprulesraid":
				DEEPBLUE_DROP_RULES_RAID = Boolean.parseBoolean(pValue);
				break;
			case "guardattackaggromob":
				GUARD_ATTACK_AGGRO_MOB = Boolean.parseBoolean(pValue);
				break;
			case "maximumslotsfornodwarf":
				INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsfordwarf":
				INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsforgmplayer":
				INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
				break;
			case "maximumslotsforquestitems":
				INVENTORY_MAXIMUM_QUEST_ITEMS = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfornodwarf":
				WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfordwarf":
				WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsforclan":
				WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
				break;
			case "enchantchanceelementstone":
				ENCHANT_CHANCE_ELEMENT_STONE = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementcrystal":
				ENCHANT_CHANCE_ELEMENT_CRYSTAL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementjewel":
				ENCHANT_CHANCE_ELEMENT_JEWEL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementenergy":
				ENCHANT_CHANCE_ELEMENT_ENERGY = Double.parseDouble(pValue);
				break;
			case "augmentationngskillchance":
				AUGMENTATION_NG_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationngglowchance":
				AUGMENTATION_NG_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidskillchance":
				AUGMENTATION_MID_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidglowchance":
				AUGMENTATION_MID_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighskillchance":
				AUGMENTATION_HIGH_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighglowchance":
				AUGMENTATION_HIGH_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopskillchance":
				AUGMENTATION_TOP_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopglowchance":
				AUGMENTATION_TOP_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationbasestatchance":
				AUGMENTATION_BASESTAT_CHANCE = Integer.parseInt(pValue);
				break;
			case "hpregenmultiplier":
				HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "mpregenmultiplier":
				MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "cpregenmultiplier":
				CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidhpregenmultiplier":
				RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidmpregenmultiplier":
				RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidpdefencemultiplier":
				RAID_PDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmdefencemultiplier":
				RAID_MDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidpattackmultiplier":
				RAID_PATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmattackmultiplier":
				RAID_MATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidminionrespawntime":
				RAID_MINION_RESPAWN_TIMER = Integer.parseInt(pValue);
				break;
			case "raidchaostime":
				RAID_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "grandchaostime":
				GRAND_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "minionchaostime":
				MINION_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "startingadena":
				STARTING_ADENA = Long.parseLong(pValue);
				break;
			case "startinglevel":
				STARTING_LEVEL = Byte.parseByte(pValue);
				break;
			case "startingsp":
				STARTING_SP = Integer.parseInt(pValue);
				break;
			case "unstuckinterval":
				UNSTUCK_INTERVAL = Integer.parseInt(pValue);
				break;
			case "teleportwatchdogtimeout":
				TELEPORT_WATCHDOG_TIMEOUT = Integer.parseInt(pValue);
				break;
			case "playerspawnprotection":
				PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
				break;
			case "playerfakedeathupprotection":
				PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
				break;
			case "partyxpcutoffmethod":
				PARTY_XP_CUTOFF_METHOD = pValue;
				break;
			case "partyxpcutoffpercent":
				PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
				break;
			case "partyxpcutofflevel":
				PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
				break;
			case "respawnrestorecp":
				RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestorehp":
				RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestoremp":
				RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
				break;
			case "maxpvtstoresellslotsdwarf":
				MAX_PVTSTORESELL_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstoresellslotsother":
				MAX_PVTSTORESELL_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsdwarf":
				MAX_PVTSTOREBUY_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsother":
				MAX_PVTSTOREBUY_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "storeskillcooltime":
				STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "subclassstoreskillcooltime":
				SUBCLASS_STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "announcemammonspawn":
				ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(pValue);
				break;
			case "altgametiredness":
				ALT_GAME_TIREDNESS = Boolean.parseBoolean(pValue);
				break;
			case "enablefallingdamage":
				ENABLE_FALLING_DAMAGE = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreation":
				ALT_GAME_CREATION = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreationspeed":
				ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
				break;
			case "altgamecreationxprate":
				ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationrarexpsprate":
				ALT_GAME_CREATION_RARE_XPSP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationsprate":
				ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
				break;
			case "altweightlimit":
				ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
				break;
			case "altblacksmithuserecipes":
				ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(pValue);
				break;
			case "altgameskilllearn":
				ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(pValue);
				break;
			case "removecastlecirclets":
				REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(pValue);
				break;
			case "reputationscoreperkill":
				REPUTATION_SCORE_PER_KILL = Integer.parseInt(pValue);
				break;
			case "altgamecancelbyhit":
				ALT_GAME_CANCEL_BOW = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
				ALT_GAME_CANCEL_CAST = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
				break;
			case "altshieldblocks":
				ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(pValue);
				break;
			case "altperfectshieldblockrate":
				ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue);
				break;
			case "delevel":
				ALT_GAME_DELEVEL = Boolean.parseBoolean(pValue);
				break;
			case "magicfailures":
				ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(pValue);
				break;
			case "altmobagroinpeacezone":
				ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altgameexponentxp":
				ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
				break;
			case "altgameexponentsp":
				ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
				break;
			case "allowclassmasters":
				ALLOW_CLASS_MASTERS = Boolean.parseBoolean(pValue);
				break;
			case "allowentiretree":
				ALLOW_ENTIRE_TREE = Boolean.parseBoolean(pValue);
				break;
			case "alternateclassmaster":
				ALTERNATE_CLASS_MASTER = Boolean.parseBoolean(pValue);
				break;
			case "altpartyrange":
				ALT_PARTY_RANGE = Integer.parseInt(pValue);
				break;
			case "altpartyrange2":
				ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
				break;
			case "altleavepartyleader":
				ALT_LEAVE_PARTY_LEADER = Boolean.parseBoolean(pValue);
				break;
			case "craftingenabled":
				IS_CRAFTING_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "craftmasterwork":
				CRAFT_MASTERWORK = Boolean.parseBoolean(pValue);
				break;
			case "lifecrystalneeded":
				LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(pValue);
				break;
			case "autoloot":
				AUTO_LOOT = Boolean.parseBoolean(pValue);
				break;
			case "autolootraids":
				AUTO_LOOT_RAIDS = Boolean.parseBoolean(pValue);
				break;
			case "autolootherbs":
				AUTO_LOOT_HERBS = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanbekilledinpeacezone":
				ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanshop":
				ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusegk":
				ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanteleport":
				ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercantrade":
				ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusewarehouse":
				ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.parseBoolean(pValue);
				break;
			case "maxpersonalfamepoints":
				MAX_PERSONAL_FAME_POINTS = Integer.parseInt(pValue);
				break;
			case "fortresszonefametaskfrequency":
				FORTRESS_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "fortresszonefameaquirepoints":
				FORTRESS_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "castlezonefametaskfrequency":
				CASTLE_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "castlezonefameaquirepoints":
				CASTLE_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "altcastlefordawn":
				ALT_GAME_CASTLE_DAWN = Boolean.parseBoolean(pValue);
				break;
			case "altcastlefordusk":
				ALT_GAME_CASTLE_DUSK = Boolean.parseBoolean(pValue);
				break;
			case "altrequireclancastle":
				ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(pValue);
				break;
			case "altfreeteleporting":
				ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasswithoutquests":
				ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasseverywhere":
				ALT_GAME_SUBCLASS_EVERYWHERE = Boolean.parseBoolean(pValue);
				break;
			case "altmemberscanwithdrawfromclanwh":
				ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(pValue);
				break;
			case "dwarfrecipelimit":
				DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "commonrecipelimit":
				COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "championenable":
				L2JMOD_CHAMPION_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "championfrequency":
				L2JMOD_CHAMPION_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "championminlevel":
				L2JMOD_CHAMP_MIN_LVL = Integer.parseInt(pValue);
				break;
			case "championmaxlevel":
				L2JMOD_CHAMP_MAX_LVL = Integer.parseInt(pValue);
				break;
			case "championhp":
				L2JMOD_CHAMPION_HP = Integer.parseInt(pValue);
				break;
			case "championhpregen":
				L2JMOD_CHAMPION_HP_REGEN = Float.parseFloat(pValue);
				break;
			case "championrewards":
				L2JMOD_CHAMPION_REWARDS = Integer.parseInt(pValue);
				break;
			case "championadenasrewards":
				L2JMOD_CHAMPION_ADENAS_REWARDS = Float.parseFloat(pValue);
				break;
			case "championatk":
				L2JMOD_CHAMPION_ATK = Float.parseFloat(pValue);
				break;
			case "championspdatk":
				L2JMOD_CHAMPION_SPD_ATK = Float.parseFloat(pValue);
				break;
			case "championrewardlowerlvlitemchance":
				L2JMOD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "championrewardhigherlvlitemchance":
				L2JMOD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "championrewarditemid":
				L2JMOD_CHAMPION_REWARD_ID = Integer.parseInt(pValue);
				break;
			case "championrewarditemqty":
				L2JMOD_CHAMPION_REWARD_QTY = Integer.parseInt(pValue);
				break;
			case "championenableininstances":
				L2JMOD_CHAMPION_ENABLE_IN_INSTANCES = Boolean.parseBoolean(pValue);
				break;
			case "allowwedding":
				L2JMOD_ALLOW_WEDDING = Boolean.parseBoolean(pValue);
				break;
			case "weddingprice":
				L2JMOD_WEDDING_PRICE = Integer.parseInt(pValue);
				break;
			case "weddingpunishinfidelity":
				L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(pValue);
				break;
			case "weddingteleport":
				L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "weddingteleportprice":
				L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(pValue);
				break;
			case "weddingteleportduration":
				L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(pValue);
				break;
			case "weddingallowsamesex":
				L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(pValue);
				break;
			case "weddingformalwear":
				L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(pValue);
				break;
			case "weddingdivorcecosts":
				L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(pValue);
				break;
			case "tvteventenabled":
				TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "tvteventinterval":
				TVT_EVENT_INTERVAL = pValue.split(",");
				break;
			case "tvteventparticipationtime":
				TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventrunningtime":
				TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventparticipationnpcid":
				TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
				break;
			case "enablewarehousesortingclan":
				L2JMOD_ENABLE_WAREHOUSESORTING_CLAN = Boolean.parseBoolean(pValue);
				break;
			case "enablewarehousesortingprivate":
				L2JMOD_ENABLE_WAREHOUSESORTING_PRIVATE = Boolean.parseBoolean(pValue);
				break;
			case "enablemanapotionsupport":
				L2JMOD_ENABLE_MANA_POTIONS_SUPPORT = Boolean.parseBoolean(pValue);
				break;
			case "displayservertime":
				L2JMOD_DISPLAY_SERVER_TIME = Boolean.parseBoolean(pValue);
				break;
			case "antifeedenable":
				L2JMOD_ANTIFEED_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddualbox":
				L2JMOD_ANTIFEED_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddisconnectedasdualbox":
				L2JMOD_ANTIFEED_DISCONNECTED_AS_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeedinterval":
				L2JMOD_ANTIFEED_INTERVAL = 1000 * Integer.parseInt(pValue);
				break;
			case "cangmdropequipment":
				KARMA_DROP_GM = Boolean.parseBoolean(pValue);
				break;
			case "awardpkkillpvppoint":
				KARMA_AWARD_PK_KILL = Boolean.parseBoolean(pValue);
				break;
			case "minimumpkrequiredtodrop":
				KARMA_PK_LIMIT = Integer.parseInt(pValue);
				break;
			case "pvpvsnormaltime":
				PVP_NORMAL_TIME = Integer.parseInt(pValue);
				break;
			case "pvpvspvptime":
				PVP_PVP_TIME = Integer.parseInt(pValue);
				break;
			case "globalchat":
				DEFAULT_GLOBAL_CHAT = pValue;
				break;
			case "tradechat":
				DEFAULT_TRADE_CHAT = pValue;
				break;
			default:
				try
				{
					// TODO: stupid GB configs...
					if (!pName.startsWith("Interval_") && !pName.startsWith("Random_"))
					{
						pName = pName.toUpperCase();
					}
					Field clazField = Config.class.getField(pName);
					int modifiers = clazField.getModifiers();
					// just in case :)
					if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers))
					{
						throw new SecurityException("Cannot modify non public, non static or final config!");
					}
					
					if (clazField.getType() == int.class)
					{
						clazField.setInt(clazField, Integer.parseInt(pValue));
					}
					else if (clazField.getType() == short.class)
					{
						clazField.setShort(clazField, Short.parseShort(pValue));
					}
					else if (clazField.getType() == byte.class)
					{
						clazField.setByte(clazField, Byte.parseByte(pValue));
					}
					else if (clazField.getType() == long.class)
					{
						clazField.setLong(clazField, Long.parseLong(pValue));
					}
					else if (clazField.getType() == float.class)
					{
						clazField.setFloat(clazField, Float.parseFloat(pValue));
					}
					else if (clazField.getType() == double.class)
					{
						clazField.setDouble(clazField, Double.parseDouble(pValue));
					}
					else if (clazField.getType() == boolean.class)
					{
						clazField.setBoolean(clazField, Boolean.parseBoolean(pValue));
					}
					else if (clazField.getType() == String.class)
					{
						clazField.set(clazField, pValue);
					}
					else
					{
						return false;
					}
				}
				catch (NoSuchFieldException e)
				{
					return false;
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, "", e);
					return false;
				}
		}
		return true;
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.<br>
	 * Check {@link #HEXID_FILE}.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 */
	public static void saveHexid(int serverId, String hexId)
	{
		Config.saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 * @param fileName name of the L2Properties file
	 */
	public static void saveHexid(int serverId, String hexId, String fileName)
	{
		try
		{
			Properties hexSetting = new Properties();
			File file = new File(fileName);
			// Create a new empty file only if it doesn't exist
			file.createNewFile();
			try (OutputStream out = new FileOutputStream(file))
			{
				hexSetting.setProperty("ServerID", String.valueOf(serverId));
				hexSetting.setProperty("HexID", hexId);
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			_log.warning(StringUtil.concat("Failed to save hex id to ", fileName, " File."));
			_log.warning("Config: " + e.getMessage());
		}
	}
	
	/**
	 * Loads flood protector configurations.
	 * @param properties the properties object containing the actual values of the flood protector configs
	 */
	private static void loadFloodProtectorConfigs(final PropertiesParser properties)
	{
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_USE_ITEM, "UseItem", 4);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", 42);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_FIREWORK, "Firework", 42);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", 16);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", 100);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", 5);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SUBCLASS, "Subclass", 20);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", 10);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", 5);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", 1);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", 10);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", 3);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANOR, "Manor", 30);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SENDMAIL, "SendMail", 100);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", 30);
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_AUCTION, "ItemAuction", 9);
	}
	
	/**
	 * Loads single flood protector configuration.
	 * @param properties properties file reader
	 * @param config flood protector configuration instance
	 * @param configString flood protector configuration string that determines for which flood protector configuration should be read
	 * @param defaultInterval default flood protector interval
	 */
	private static void loadFloodProtectorConfig(final PropertiesParser properties, final FloodProtectorConfig config, final String configString, final int defaultInterval)
	{
		config.FLOOD_PROTECTION_INTERVAL = properties.getInt(StringUtil.concat("FloodProtector", configString, "Interval"), defaultInterval);
		config.LOG_FLOODING = properties.getBoolean(StringUtil.concat("FloodProtector", configString, "LogFlooding"), false);
		config.PUNISHMENT_LIMIT = properties.getInt(StringUtil.concat("FloodProtector", configString, "PunishmentLimit"), 0);
		config.PUNISHMENT_TYPE = properties.getString(StringUtil.concat("FloodProtector", configString, "PunishmentType"), "none");
		config.PUNISHMENT_TIME = properties.getInt(StringUtil.concat("FloodProtector", configString, "PunishmentTime"), 0) * 60000;
	}
	
	public static int getServerTypeId(String[] serverTypes)
	{
		int tType = 0;
		for (String cType : serverTypes)
		{
			switch (cType.trim().toLowerCase())
			{
				case "Normal":
					tType |= 0x01;
					break;
				case "Relax":
					tType |= 0x02;
					break;
				case "Test":
					tType |= 0x04;
					break;
				case "NoLabel":
					tType |= 0x08;
					break;
				case "Restricted":
					tType |= 0x10;
					break;
				case "Event":
					tType |= 0x20;
					break;
				case "Free":
					tType |= 0x40;
					break;
				default:
					break;
			}
		}
		return tType;
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, List<ItemHolder>> _claimItems = new HashMap<>(3);
		private final Map<Integer, List<ItemHolder>> _rewardItems = new HashMap<>(3);
		private final Map<Integer, Boolean> _allowedClassChange = new HashMap<>(3);
		
		public ClassMasterSettings(String configLine)
		{
			parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			if (configLine.isEmpty())
			{
				return;
			}
			
			final StringTokenizer st = new StringTokenizer(configLine, ";");
			
			while (st.hasMoreTokens())
			{
				// get allowed class change
				final int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				final List<ItemHolder> requiredItems = new ArrayList<>();
				// parse items needed for class change
				if (st.hasMoreTokens())
				{
					final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						final int itemId = Integer.parseInt(st3.nextToken());
						final int quantity = Integer.parseInt(st3.nextToken());
						requiredItems.add(new ItemHolder(itemId, quantity));
					}
				}
				
				_claimItems.put(job, requiredItems);
				
				final List<ItemHolder> rewardItems = new ArrayList<>();
				// parse gifts after class change
				if (st.hasMoreTokens())
				{
					final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						final int itemId = Integer.parseInt(st3.nextToken());
						final int quantity = Integer.parseInt(st3.nextToken());
						rewardItems.add(new ItemHolder(itemId, quantity));
					}
				}
				
				_rewardItems.put(job, rewardItems);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if ((_allowedClassChange == null) || !_allowedClassChange.containsKey(job))
			{
				return false;
			}
			return _allowedClassChange.get(job);
		}
		
		public List<ItemHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<ItemHolder> getRequireItems(int job)
		{
			return _claimItems.get(job);
		}
	}
	
	/**
	 * @param line the string line to parse
	 * @return a parsed float map
	 */
	private static Map<Integer, Float> parseConfigLine(String line)
	{
		String[] propertySplit = line.split(",");
		Map<Integer, Float> ret = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			ret.put(i++, Float.parseFloat(value));
		}
		return ret;
	}
	
	/**
	 * Parse a config value from its string representation to a two-dimensional int array.<br>
	 * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
	 * @param line the value of the parameter to parse
	 * @return the parsed list or {@code null} if nothing was parsed
	 */
	private static int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
		{
			// nothing to do here
			return null;
		}
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		int[] tmp;
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid entry -> \"", valueSplit[0], "\", should be itemId,itemNumber. Skipping to the next entry in the list."));
				continue;
			}
			
			tmp = new int[2];
			try
			{
				tmp[0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid itemId -> \"", valueSplit[0], "\", value must be an integer. Skipping to the next entry in the list."));
				continue;
			}
			try
			{
				tmp[1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid item number -> \"", valueSplit[1], "\", value must be an integer. Skipping to the next entry in the list."));
				continue;
			}
			result[i++] = tmp;
		}
		return result;
	}
	
	private static class IPConfigData implements IXmlReader
	{
		private static final List<String> _subnets = new ArrayList<>(5);
		private static final List<String> _hosts = new ArrayList<>(5);
		
		public IPConfigData()
		{
			load();
		}
		
		@Override
		public void load()
		{
			File f = new File(IP_CONFIG_FILE);
			if (f.exists())
			{
				LOGGER.log(Level.INFO, "Network Config: ipconfig.xml exists using manual configuration...");
				parseFile(new File(IP_CONFIG_FILE));
			}
			else
			// Auto configuration...
			{
				LOGGER.log(Level.INFO, "Network Config: ipconfig.xml doesn't exists using automatic configuration...");
				autoIpConfig();
			}
		}
		
		@Override
		public void parseDocument(Document doc)
		{
			NamedNodeMap attrs;
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("gameserver".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("define".equalsIgnoreCase(d.getNodeName()))
						{
							attrs = d.getAttributes();
							_subnets.add(attrs.getNamedItem("subnet").getNodeValue());
							_hosts.add(attrs.getNamedItem("address").getNodeValue());
							
							if (_hosts.size() != _subnets.size())
							{
								LOGGER.log(Level.WARNING, "Failed to Load " + IP_CONFIG_FILE + " File - subnets does not match server addresses.");
							}
						}
					}
					
					Node att = n.getAttributes().getNamedItem("address");
					if (att == null)
					{
						LOGGER.log(Level.WARNING, "Failed to load " + IP_CONFIG_FILE + " file - default server address is missing.");
						_hosts.add("127.0.0.1");
					}
					else
					{
						_hosts.add(att.getNodeValue());
					}
					_subnets.add("0.0.0.0/0");
				}
			}
		}
		
		protected void autoIpConfig()
		{
			String externalIp = "127.0.0.1";
			try
			{
				URL autoIp = new URL("http://ip1.dynupdate.no-ip.com:8245/");
				try (BufferedReader in = new BufferedReader(new InputStreamReader(autoIp.openStream())))
				{
					externalIp = in.readLine();
				}
			}
			catch (IOException e)
			{
				LOGGER.log(Level.INFO, "Network Config: Failed to connect to api.externalip.net please check your internet connection using 127.0.0.1!");
				externalIp = "127.0.0.1";
			}
			
			try
			{
				Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
				
				while (niList.hasMoreElements())
				{
					NetworkInterface ni = niList.nextElement();
					
					if (!ni.isUp() || ni.isVirtual())
					{
						continue;
					}
					
					if (!ni.isLoopback() && ((ni.getHardwareAddress() == null) || (ni.getHardwareAddress().length != 6)))
					{
						continue;
					}
					
					for (InterfaceAddress ia : ni.getInterfaceAddresses())
					{
						if (ia.getAddress() instanceof Inet6Address)
						{
							continue;
						}
						
						final String hostAddress = ia.getAddress().getHostAddress();
						final int subnetPrefixLength = ia.getNetworkPrefixLength();
						final int subnetMaskInt = IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0) << (32 - subnetPrefixLength);
						final int hostAddressInt = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
						final int subnetAddressInt = hostAddressInt & subnetMaskInt;
						final String subnetAddress = ((subnetAddressInt >> 24) & 0xFF) + "." + ((subnetAddressInt >> 16) & 0xFF) + "." + ((subnetAddressInt >> 8) & 0xFF) + "." + (subnetAddressInt & 0xFF);
						final String subnet = subnetAddress + '/' + subnetPrefixLength;
						if (!_subnets.contains(subnet) && !subnet.equals("0.0.0.0/0"))
						{
							_subnets.add(subnet);
							_hosts.add(hostAddress);
							LOGGER.log(Level.INFO, "Network Config: Adding new subnet: " + subnet + " address: " + hostAddress);
						}
					}
				}
				
				// External host and subnet
				_hosts.add(externalIp);
				_subnets.add("0.0.0.0/0");
				LOGGER.log(Level.INFO, "Network Config: Adding new subnet: 0.0.0.0/0 address: " + externalIp);
			}
			catch (SocketException e)
			{
				LOGGER.log(Level.INFO, "Network Config: Configuration failed please configure manually using ipconfig.xml", e);
				System.exit(0);
			}
		}
		
		protected List<String> getSubnets()
		{
			if (_subnets.isEmpty())
			{
				return Arrays.asList("0.0.0.0/0");
			}
			return _subnets;
		}
		
		protected List<String> getHosts()
		{
			if (_hosts.isEmpty())
			{
				return Arrays.asList("127.0.0.1");
			}
			return _hosts;
		}
	}
}
