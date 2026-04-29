package net.mp9.magicungathering.logic;

import net.mp9.magicungathering.attachment.SkillData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillRequirements {
    private static final Map<String, List<String>> REQUIREMENTS = new HashMap<>();
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();

    static {
        // --- ASSASSIN TREE ---
        // page 1
        addReq("assassin_1speed5", "assassin_mark", "Requires 'Mark Enemy'!");
        addReq("upgrade_mark_reduced_cost", "assassin_1speed5", "Requires 'Speed +5%'!");
        addReq("upgrade_mark_increased_time", "assassin_1speed5", "Requires 'Speed +5%'!");
        addReq("upgrade_execute_time", "assassin_2speed5", "Requires 'Speed +5%'!");
        addReq("assassin_2speed5", "upgrade_blink_weakness", "Requires 'Weakening Blink'!");

        // page 2
        addReq("assassin_blink", "upgrade_mark_increased_time", "Requires 'Mark Time Upgrade'!");
        addReq("assassin_execute", "assassin_blink", "Requires 'Blink'!");
        addReq("assassin_1damage5", "assassin_blink", "Requires 'Blink'!");
        addReq("upgrade_blink_weakness", "assassin_execute", "Requires 'Execute'!");
        addReq("shadow_step", "upgrade_execute_level", "Requires 'Execute Strength 3'!");
        addReq("upgrade_execute_level", "assassin_execute,smoke_screen,assassin_manaregen2", "Requires one of the surrounding upgrades!");
        addReq("smoke_screen", "upgrade_execute_level,assassin_1speed5armor1,upgrade_smoke_screen_length", "Requires one of the surrounding upgrades!");
        addReq("assassin_1speed5armor1", "smoke_screen,assassin_1damage5,upgrade_blink_time", "Requires one of the surrounding upgrades!");
        addReq("upgrade_blink_time", "assassin_1speed5armor1,assassin_1maxmana5", "Requires one of the surrounding upgrades!");

        // page 3
        addReq("assassin_manaregen2", "upgrade_execute_level,assassin_speed5", "Requires one of the surrounding upgrades!");
        addReq("upgrade_smoke_screen_length", "smoke_screen,assassin_1armor1", "Requires one of the surrounding upgrades!");
        addReq("assassin_vanish", "assassin_1maxmana5,assassin_2armor1", "Requires one of the surrounding upgrades!");
        addReq("assassin_1maxmana5", "upgrade_blink_time,assassin_vanish,assassin_haemorrhage", "Requires one of the surrounding upgrades!");
        addReq("upgrade_blink_reduced_cost", "assassin_speed5,assassin_heart1speed5", "Requires one of the surrounding upgrades!");
        addReq("assassin_speed5", "upgrade_blink_reduced_cost,assassin_manaregen2", "Requires one of the surrounding upgrades!");
        addReq("assassin_1armor1", "upgrade_smoke_screen_length,assassin_2armor1,upgrade_smoke_screen_poison", "Requires one of the surrounding upgrades!");
        addReq("assassin_2armor1", "assassin_vanish,assassin_1armor1,upgrade_vanish_no_weakness", "Requires one of the surrounding upgrades!");
        addReq("assassin_haemorrhage", "assassin_1maxmana5,assassin_2maxmana5", "Requires one of the surrounding upgrades!");
        addReq("assassin_heart1speed5", "upgrade_blink_reduced_cost,assassin_maxmana10", "Requires one of the surrounding upgrades!");
        addReq("upgrade_smoke_screen_poison", "assassin_1armor1", "Requires 'Toughen Up'!");
        addReq("upgrade_vanish_no_weakness", "assassin_2armor1,assassin_2maxmana5,assassin_2speed5armor1", "Requires one of the surrounding upgrades!");
        addReq("assassin_2maxmana5", "assassin_haemorrhage,upgrade_vanish_no_weakness,upgrade_vanish_lifesteal", "Requires one of the surrounding upgrades!");

        // page 4
        addReq("assassin_maxmana10", "assassin_heart1speed5,assassin_manaregen5", "Requires one of the surrounding upgrades!");
        addReq("assassin_manaregen5", "assassin_maxmana10,assassin_maxmana15heart1", "Requires one of the surrounding upgrades!");
        addReq("assassin_maxmana15heart1", "assassin_manaregen5,assassin_2speed5armor1", "Requires one of the surrounding upgrades!");
        addReq("assassin_2speed5armor1", "assassin_maxmana15heart1,upgrade_vanish_no_weakness,upgrade_vanish_lifesteal", "Requires one of the surrounding upgrades!");
        addReq("upgrade_vanish_lifesteal", "assassin_2speed5armor1,assassin_2maxmana5", "Requires one of the surrounding upgrades!");
        addReq("upgrade_execute_bones", "upgrade_execute_time2", "Requires 'Ultimate Executioner'!");
        addReq("upgrade_execute_time2", "assassin_manaregen5", "Requires 'Knowledge'!");
        addReq("assassin_damage15heart1", "assassin_maxmana15heart1", "Requires 'My Knowledge For Life'!");
        addReq("upgrade_haemorrhage_time", "assassin_damage15heart1", "Requires 'My Life for Damage'!");
        addReq("upgrade_haemorrhage_damage", "upgrade_haemorrhage_time", "Requires 'Long Live the Haemorrhage'!");


        // --- WARRIOR TREE ---
        addReq("warrior_dash", "warrior_mark", "Requires Warrior Mark!");
    }

    private static void addReq(String skillId, String parentIds, String error) {
        REQUIREMENTS.put(skillId, List.of(parentIds.split(",")));
        ERROR_MESSAGES.put(skillId, error);
    }

    public static RequirementResult check(String skillId, SkillData data) {
        if (!REQUIREMENTS.containsKey(skillId)) return RequirementResult.PASS;

        List<String> parents = REQUIREMENTS.get(skillId);
        boolean hasRequirement = false;

        for (String id : parents) {
            if (data.hasSkill(id)) {
                hasRequirement = true;
                break;
            }
        }

        if (!hasRequirement) {
            return new RequirementResult(false, ERROR_MESSAGES.getOrDefault(skillId, "Locked!"));
        }
        return RequirementResult.PASS;
    }

    public record RequirementResult(boolean success, String error) {
        public static final RequirementResult PASS = new RequirementResult(true, "");
    }
}