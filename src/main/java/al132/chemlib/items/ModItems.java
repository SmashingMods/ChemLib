package al132.chemlib.items;

import al132.chemlib.ChemLib;
import al132.chemlib.chemistry.ChemicalStack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModItems {

    public static List<BaseItem> items = new ArrayList<>();

    //Generated from elements.csv
    public static ElementItem hydrogen = new ElementItem("hydrogen", 1, "H");
    public static ElementItem helium = new ElementItem("helium", 2, "He");
    public static ElementItem lithium = new ElementItem("lithium", 3, "Li");

    public static ElementItem beryllium = new ElementItem("beryllium", 4, "Be");
    public static ElementItem boron = new ElementItem("boron", 5, "B");
    public static ElementItem carbon = new ElementItem("carbon", 6, "C");
    public static ElementItem nitrogen = new ElementItem("nitrogen", 7, "N");
    public static ElementItem oxygen = new ElementItem("oxygen", 8, "O");
    public static ElementItem fluorine = new ElementItem("fluorine", 9, "F");
    public static ElementItem neon = new ElementItem("neon", 10, "Ne");
    public static ElementItem sodium = new ElementItem("sodium", 11, "Na");
    public static ElementItem magnesium = new ElementItem("magnesium", 12, "Mg");
    public static ElementItem aluminum = new ElementItem("aluminum", 13, "Al");
    public static ElementItem silicon = new ElementItem("silicon", 14, "Si");
    public static ElementItem phosphorus = new ElementItem("phosphorus", 15, "P");
    public static ElementItem sulfur = new ElementItem("sulfur", 16, "S");
    public static ElementItem chlorine = new ElementItem("chlorine", 17, "Cl");
    public static ElementItem argon = new ElementItem("argon", 18, "Ar");
    public static ElementItem potassium = new ElementItem("potassium", 19, "K");
    public static ElementItem calcium = new ElementItem("calcium", 20, "Ca");
    public static ElementItem scandium = new ElementItem("scandium", 21, "Sc");
    public static ElementItem titanium = new ElementItem("titanium", 22, "Ti");
    public static ElementItem vanadium = new ElementItem("vanadium", 23, "V");
    public static ElementItem chromium = new ElementItem("chromium", 24, "Cr");
    public static ElementItem manganese = new ElementItem("manganese", 25, "Mn");
    public static ElementItem iron = new ElementItem("iron", 26, "Fe");
    public static ElementItem cobalt = new ElementItem("cobalt", 27, "Co");
    public static ElementItem nickel = new ElementItem("nickel", 28, "Ni");
    public static ElementItem copper = new ElementItem("copper", 29, "Cu");
    public static ElementItem zinc = new ElementItem("zinc", 30, "Zn");
    public static ElementItem gallium = new ElementItem("gallium", 31, "Ga");
    public static ElementItem germanium = new ElementItem("germanium", 32, "Ge");
    public static ElementItem arsenic = new ElementItem("arsenic", 33, "As");
    public static ElementItem selenium = new ElementItem("selenium", 34, "Se");
    public static ElementItem bromine = new ElementItem("bromine", 35, "Br");
    public static ElementItem krypton = new ElementItem("krypton", 36, "Kr");
    public static ElementItem rubidium = new ElementItem("rubidium", 37, "Rb");
    public static ElementItem strontium = new ElementItem("strontium", 38, "Sr");
    public static ElementItem yttrium = new ElementItem("yttrium", 39, "Y");
    public static ElementItem zirconium = new ElementItem("zirconium", 40, "Zr");
    public static ElementItem niobium = new ElementItem("niobium", 41, "Nb");
    public static ElementItem molybdenum = new ElementItem("molybdenum", 42, "Mo");
    public static ElementItem technetium = new ElementItem("technetium", 43, "Tc");
    public static ElementItem ruthenium = new ElementItem("ruthenium", 44, "Ru");
    public static ElementItem rhodium = new ElementItem("rhodium", 45, "Rh");
    public static ElementItem palladium = new ElementItem("palladium", 46, "Pd");
    public static ElementItem silver = new ElementItem("silver", 47, "Ag");
    public static ElementItem cadmium = new ElementItem("cadmium", 48, "Cd");
    public static ElementItem indium = new ElementItem("indium", 49, "In");
    public static ElementItem tin = new ElementItem("tin", 50, "Sn");
    public static ElementItem antimony = new ElementItem("antimony", 51, "Sb");
    public static ElementItem tellurium = new ElementItem("tellurium", 52, "Te");
    public static ElementItem iodine = new ElementItem("iodine", 53, "I");
    public static ElementItem xenon = new ElementItem("xenon", 54, "Xe");
    public static ElementItem cesium = new ElementItem("cesium", 55, "Cs");
    public static ElementItem barium = new ElementItem("barium", 56, "Ba");
    public static ElementItem lanthanum = new ElementItem("lanthanum", 57, "La");
    public static ElementItem cerium = new ElementItem("cerium", 58, "Ce");
    public static ElementItem praseodymium = new ElementItem("praseodymium", 59, "Pr");
    public static ElementItem neodymium = new ElementItem("neodymium", 60, "Nd");
    public static ElementItem promethium = new ElementItem("promethium", 61, "Pm");
    public static ElementItem samarium = new ElementItem("samarium", 62, "Sm");
    public static ElementItem europium = new ElementItem("europium", 63, "Eu");
    public static ElementItem gadolinium = new ElementItem("gadolinium", 64, "Gd");
    public static ElementItem terbium = new ElementItem("terbium", 65, "Tb");
    public static ElementItem dysprosium = new ElementItem("dysprosium", 66, "Dy");
    public static ElementItem holmium = new ElementItem("holmium", 67, "Ho");
    public static ElementItem erbium = new ElementItem("erbium", 68, "Er");
    public static ElementItem thulium = new ElementItem("thulium", 69, "Tm");
    public static ElementItem ytterbium = new ElementItem("ytterbium", 70, "Yb");
    public static ElementItem lutetium = new ElementItem("lutetium", 71, "Lu");
    public static ElementItem hafnium = new ElementItem("hafnium", 72, "Hf");
    public static ElementItem tantalum = new ElementItem("tantalum", 73, "Ta");
    public static ElementItem tungsten = new ElementItem("tungsten", 74, "W");
    public static ElementItem rhenium = new ElementItem("rhenium", 75, "Re");
    public static ElementItem osmium = new ElementItem("osmium", 76, "Os");
    public static ElementItem iridium = new ElementItem("iridium", 77, "Ir");
    public static ElementItem platinum = new ElementItem("platinum", 78, "Pt");
    public static ElementItem gold = new ElementItem("gold", 79, "Au");
    public static ElementItem mercury = new ElementItem("mercury", 80, "Hg");
    public static ElementItem thallium = new ElementItem("thallium", 81, "Tl");
    public static ElementItem lead = new ElementItem("lead", 82, "Pb");
    public static ElementItem bismuth = new ElementItem("bismuth", 83, "Bi");
    public static ElementItem polonium = new ElementItem("polonium", 84, "Po");
    public static ElementItem astatine = new ElementItem("astatine", 85, "At");
    public static ElementItem radon = new ElementItem("radon", 86, "Rn");
    public static ElementItem francium = new ElementItem("francium", 87, "Fr");
    public static ElementItem radium = new ElementItem("radium", 88, "Ra");
    public static ElementItem actinium = new ElementItem("actinium", 89, "Ac");
    public static ElementItem thorium = new ElementItem("thorium", 90, "Th");
    public static ElementItem protactinium = new ElementItem("protactinium", 91, "Pa");
    public static ElementItem uranium = new ElementItem("uranium", 92, "U");
    public static ElementItem neptunium = new ElementItem("neptunium", 93, "Np");
    public static ElementItem plutonium = new ElementItem("plutonium", 94, "Pu");
    public static ElementItem americium = new ElementItem("americium", 95, "Am");
    public static ElementItem curium = new ElementItem("curium", 96, "Cm");
    public static ElementItem berkelium = new ElementItem("berkelium", 97, "Bk");
    public static ElementItem californium = new ElementItem("californium", 98, "Cf");
    public static ElementItem einsteinium = new ElementItem("einsteinium", 99, "Es");
    public static ElementItem fermium = new ElementItem("fermium", 100, "Fm");
    public static ElementItem mendelevium = new ElementItem("mendelevium", 101, "Md");
    public static ElementItem nobelium = new ElementItem("nobelium", 102, "No");
    public static ElementItem lawrencium = new ElementItem("lawrencium", 103, "Lr");
    public static ElementItem rutherfordium = new ElementItem("rutherfordium", 104, "Rf");
    public static ElementItem dubnium = new ElementItem("dubnium", 105, "Db");
    public static ElementItem seaborgium = new ElementItem("seaborgium", 106, "Sg");
    public static ElementItem bohrium = new ElementItem("bohrium", 107, "Bh");
    public static ElementItem hassium = new ElementItem("hassium", 108, "Hs");
    public static ElementItem meitnerium = new ElementItem("meitnerium", 109, "Mt");
    public static ElementItem darmstadtium = new ElementItem("darmstadtium", 110, "Ds");
    public static ElementItem roentgenium = new ElementItem("roentgenium", 111, "Rg");
    public static ElementItem copernicium = new ElementItem("copernicium", 112, "Cn");
    public static ElementItem nihonium = new ElementItem("nihonium", 113, "Nh");
    public static ElementItem flerovium = new ElementItem("flerovium", 114, "Fl");
    public static ElementItem moscovium = new ElementItem("moscovium", 115, "Mc");
    public static ElementItem livermorium = new ElementItem("livermorium", 116, "Lv");
    public static ElementItem tennessine = new ElementItem("tennessine", 117, "Ts");
    public static ElementItem oganesson = new ElementItem("oganesson", 118, "Og");


    public static IngotItem lithiumIngot = new IngotItem("lithium", new Color(40, 158, 86));
    public static IngotItem berylliumIngot = new IngotItem("beryllium", new Color(184, 199, 224));
    public static IngotItem boronIngot = new IngotItem("boron", new Color(154, 176, 226));
    public static IngotItem sodiumIngot = new IngotItem("sodium", new Color(211, 198, 131));
    public static IngotItem magnesiumIngot = new IngotItem("magnesium", new Color(237, 178, 173));
    public static IngotItem aluminumIngot = new IngotItem("aluminum", new Color(247, 110, 69));
    public static IngotItem siliconIngot = new IngotItem("silicon", new Color(173, 178, 121));
    public static IngotItem potassiumIngot = new IngotItem("potassium", new Color(198, 152, 95));
    public static IngotItem calciumIngot = new IngotItem("calcium", new Color(219, 210, 199));
    public static IngotItem scandiumIngot = new IngotItem("scandium", new Color(252, 255, 99));
    public static IngotItem titaniumIngot = new IngotItem("titanium", new Color(99, 255, 115));
    public static IngotItem vanadiumIngot = new IngotItem("vanadium", new Color(195, 186, 242));
    public static IngotItem chromiumIngot = new IngotItem("chromium", new Color(236, 237, 218));
    public static IngotItem manganeseIngot = new IngotItem("manganese", new Color(225, 186, 242));
    public static IngotItem cobaltIngot = new IngotItem("cobalt", new Color(17, 114, 198));
    public static IngotItem nickelIngot = new IngotItem("nickel", new Color(198, 157, 162));
    public static IngotItem copperIngot = new IngotItem("copper", new Color(255, 154, 30));
    public static IngotItem zincIngot = new IngotItem("zinc", new Color(189, 196, 141));
    public static IngotItem galliumIngot = new IngotItem("gallium", new Color(122, 20, 49));
    public static IngotItem germaniumIngot = new IngotItem("germanium", new Color(104, 172, 255));
    public static IngotItem arsenicIngot = new IngotItem("arsenic", new Color(62, 145, 76));
    public static IngotItem seleniumIngot = new IngotItem("selenium", new Color(116, 62, 145));
    public static IngotItem rubidiumIngot = new IngotItem("rubidium", new Color(15, 61, 40));
    public static IngotItem strontiumIngot = new IngotItem("strontium", new Color(206, 88, 24));
    public static IngotItem yttriumIngot = new IngotItem("yttrium", new Color(206, 179, 24));
    public static IngotItem zirconiumIngot = new IngotItem("zirconium", new Color(127, 80, 22));
    public static IngotItem niobiumIngot = new IngotItem("niobium", new Color(2, 29, 255));
    public static IngotItem molybdenumIngot = new IngotItem("molybdenum", new Color(39, 0, 48));
    public static IngotItem technetiumIngot = new IngotItem("technetium", new Color(72, 170, 63));
    public static IngotItem rutheniumIngot = new IngotItem("ruthenium", new Color(255, 240, 86));
    public static IngotItem rhodiumIngot = new IngotItem("rhodium", new Color(255, 0, 80));
    public static IngotItem palladiumIngot = new IngotItem("palladium", new Color(0, 255, 169));
    public static IngotItem silverIngot = new IngotItem("silver", new Color(226, 217, 206));
    public static IngotItem cadmiumIngot = new IngotItem("cadmium", new Color(160, 147, 115));
    public static IngotItem indiumIngot = new IngotItem("indium", new Color(163, 230, 255));
    public static IngotItem tinIngot = new IngotItem("tin", new Color(132, 161, 206));
    public static IngotItem antimonyIngot = new IngotItem("antimony", new Color(193, 40, 58));
    public static IngotItem telluriumIngot = new IngotItem("tellurium", new Color(39, 91, 26));
    public static IngotItem cesiumIngot = new IngotItem("cesium", new Color(255, 148, 0));
    public static IngotItem bariumIngot = new IngotItem("barium", new Color(0, 219, 179));
    public static IngotItem lanthanumIngot = new IngotItem("lanthanum", new Color(188, 253, 255));
    public static IngotItem ceriumIngot = new IngotItem("cerium", new Color(255, 254, 211));
    public static IngotItem praseodymiumIngot = new IngotItem("praseodymium", new Color(255, 161, 0));
    public static IngotItem neodymiumIngot = new IngotItem("neodymium", new Color(38, 28, 11));
    public static IngotItem promethiumIngot = new IngotItem("promethium", new Color(105, 175, 123));
    public static IngotItem samariumIngot = new IngotItem("samarium", new Color(73, 69, 73));
    public static IngotItem europiumIngot = new IngotItem("europium", new Color(27, 211, 45));
    public static IngotItem gadoliniumIngot = new IngotItem("gadolinium", new Color(123, 50, 208));
    public static IngotItem terbiumIngot = new IngotItem("terbium", new Color(3, 37, 118));
    public static IngotItem dysprosiumIngot = new IngotItem("dysprosium", new Color(73, 0, 219));
    public static IngotItem holmiumIngot = new IngotItem("holmium", new Color(62, 255, 56));
    public static IngotItem erbiumIngot = new IngotItem("erbium", new Color(194, 214, 215));
    public static IngotItem thuliumIngot = new IngotItem("thulium", new Color(234, 178, 178));
    public static IngotItem ytterbiumIngot = new IngotItem("ytterbium", new Color(255, 76, 219));
    public static IngotItem lutetiumIngot = new IngotItem("lutetium", new Color(175, 0, 219));
    public static IngotItem hafniumIngot = new IngotItem("hafnium", new Color(69, 81, 233));
    public static IngotItem tantalumIngot = new IngotItem("tantalum", new Color(108, 142, 110));
    public static IngotItem tungstenIngot = new IngotItem("tungsten", new Color(120, 128, 140));
    public static IngotItem rheniumIngot = new IngotItem("rhenium", new Color(199, 226, 89));
    public static IngotItem osmiumIngot = new IngotItem("osmium", new Color(102, 129, 173));
    public static IngotItem iridiumIngot = new IngotItem("iridium", new Color(215, 242, 238));
    public static IngotItem platinumIngot = new IngotItem("platinum", new Color(114, 202, 229));
    public static IngotItem thalliumIngot = new IngotItem("thallium", new Color(103, 50, 25));
    public static IngotItem leadIngot = new IngotItem("lead", new Color(186, 135, 193));
    public static IngotItem bismuthIngot = new IngotItem("bismuth", new Color(252, 171, 40));
    public static IngotItem poloniumIngot = new IngotItem("polonium", new Color(138, 87, 85));
    public static IngotItem astatineIngot = new IngotItem("astatine", new Color(120, 128, 213));
    public static IngotItem franciumIngot = new IngotItem("francium", new Color(81, 114, 198));
    public static IngotItem radiumIngot = new IngotItem("radium", new Color(255, 181, 221));
    public static IngotItem actiniumIngot = new IngotItem("actinium", new Color(14, 182, 145));
    public static IngotItem thoriumIngot = new IngotItem("thorium", new Color(56, 79, 75));
    public static IngotItem protactiniumIngot = new IngotItem("protactinium", new Color(204, 233, 2));
    public static IngotItem uraniumIngot = new IngotItem("uranium", new Color(93, 178, 19));
    public static IngotItem neptuniumIngot = new IngotItem("neptunium", new Color(32, 20, 158));
    public static IngotItem plutoniumIngot = new IngotItem("plutonium", new Color(211, 211, 209));
    public static IngotItem americiumIngot = new IngotItem("americium", new Color(237, 124, 75));
    public static IngotItem curiumIngot = new IngotItem("curium", new Color(229, 110, 149));
    public static IngotItem berkeliumIngot = new IngotItem("berkelium", new Color(44, 66, 49));
    public static IngotItem californiumIngot = new IngotItem("californium", new Color(175, 182, 16));
    public static IngotItem einsteiniumIngot = new IngotItem("einsteinium", new Color(192, 210, 95));
    public static IngotItem fermiumIngot = new IngotItem("fermium", new Color(74, 226, 83));
    public static IngotItem mendeleviumIngot = new IngotItem("mendelevium", new Color(175, 176, 249));
    public static IngotItem nobeliumIngot = new IngotItem("nobelium", new Color(94, 44, 52));
    public static IngotItem lawrenciumIngot = new IngotItem("lawrencium", new Color(216, 45, 92));
    public static IngotItem rutherfordiumIngot = new IngotItem("rutherfordium", new Color(240, 61, 22));
    public static IngotItem dubniumIngot = new IngotItem("dubnium", new Color(11, 112, 108));
    public static IngotItem seaborgiumIngot = new IngotItem("seaborgium", new Color(158, 49, 74));
    public static IngotItem bohriumIngot = new IngotItem("bohrium", new Color(166, 251, 51));
    public static IngotItem hassiumIngot = new IngotItem("hassium", new Color(78, 5, 51));
    public static IngotItem meitneriumIngot = new IngotItem("meitnerium", new Color(169, 138, 37));
    public static IngotItem darmstadtiumIngot = new IngotItem("darmstadtium", new Color(14, 144, 190));
    public static IngotItem roentgeniumIngot = new IngotItem("roentgenium", new Color(150, 90, 90));
    public static IngotItem coperniciumIngot = new IngotItem("copernicium", new Color(160, 40, 240));
    public static IngotItem nihoniumIngot = new IngotItem("nihonium", new Color(220, 250, 180));
    public static IngotItem fleroviumIngot = new IngotItem("flerovium", new Color(200, 180, 254));
    public static IngotItem moscoviumIngot = new IngotItem("moscovium", new Color(250, 180, 200));
    public static IngotItem livermoriumIngot = new IngotItem("livermorium", new Color(250, 250, 200));
    public static IngotItem tennessineIngot = new IngotItem("tennessine", new Color(150, 250, 250));
    public static IngotItem oganessonIngot = new IngotItem("oganesson", new Color(250, 150, 250));


    /*
    public static CompoundItem carbonDioxide = new CompoundItem("carbon_dioxide", new Color(50, 250, 10),
            Lists.newArrayList(new ChemicalStack(carbon, 1),
                    (new ChemicalStack(oxygen, 2))));
*/
    public static List<CompoundItem> parseCompounds() {
        List<CompoundItem> output = new ArrayList<>();
        InputStream is = ChemLib.class.getResourceAsStream("/assets/chemlib/compounds.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        JsonObject json = JSONUtils.parse(reader);
        for (JsonElement el : (JsonArray) json.get("compounds")) {
            JsonObject obj = el.getAsJsonObject();
            String name = obj.get("name").getAsString();
            int[] color = Arrays.stream(obj.get("color")
                    .getAsString()
                    .split(","))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            JsonArray components = obj.getAsJsonArray("components");
            List<ChemicalStack> componentStacks = new ArrayList<>();
            for (JsonElement component : components) {

                JsonObject componentObj = component.getAsJsonObject();
                String componentName = componentObj.get("name").getAsString();
                int count = componentObj.has("count") ? componentObj.get("count").getAsInt() : 1;
                ChemicalStack[] stack = {null};
                ChemicalStack.lookup(componentName).ifPresent(chemical -> stack[0] = (new ChemicalStack(chemical, count)));
                if (stack[0] != null) componentStacks.add(stack[0]);
            }
            //TODO
            int shiftedSlots = 0;
            int burnTime = 0;
            if(obj.has("shifted_slots")) shiftedSlots = obj.get("shifted_slots").getAsInt();
            if(obj.has("burn_time")) burnTime = obj.get("burn_time").getAsInt();
            CompoundItem compound = new CompoundItem(name, new Color(color[0], color[1], color[2]), componentStacks,shiftedSlots);
            compound.burnTime = burnTime;
            output.add(compound);
        }
        return output;
    }
}