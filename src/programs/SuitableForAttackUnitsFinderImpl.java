package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        if (unitsByRow == null || unitsByRow.isEmpty()) {
            return suitableUnits;
        }

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }

            if (!isLeftArmyTarget) {
                suitableUnits.add(row.get(row.size() - 1));
            } else {
                suitableUnits.add(row.get(0));
            }
        }

        return suitableUnits;
    }
}