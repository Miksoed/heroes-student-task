package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty()) {
            Army emptyArmy = new Army(new ArrayList<>());
            emptyArmy.setPoints(0);
            return emptyArmy;
        }

        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((u1, u2) -> {
            double eff1 = calculateEfficiency(u1);
            double eff2 = calculateEfficiency(u2);
            return Double.compare(eff2, eff1);
        });

        List<Unit> armyUnits = new ArrayList<>();
        int remainingPoints = maxPoints;
        Map<String, Integer> typeCount = new HashMap<>();
        final int MAX_PER_TYPE = 11;

        for (Unit unit : sortedUnits) {
            if (remainingPoints <= 0) break;

            String unitType = unit.getUnitType();
            int currentCount = typeCount.getOrDefault(unitType, 0);

            if (currentCount >= MAX_PER_TYPE) continue;

            int unitCost = unit.getCost();
            if (unitCost <= 0) continue;

            int maxCanTake = Math.min(
                    MAX_PER_TYPE - currentCount,
                    remainingPoints / unitCost
            );

            for (int i = 0; i < maxCanTake; i++) {
                armyUnits.add(unit);
                remainingPoints -= unitCost;
                currentCount++;

                if (remainingPoints < unitCost) break;
            }

            typeCount.put(unitType, currentCount);
        }

        ensureMinimumUnits(armyUnits, sortedUnits, 15);

        Army army = new Army(armyUnits);
        int totalCost = calculateTotalCost(armyUnits);
        army.setPoints(totalCost);

        return army;
    }

    private void ensureMinimumUnits(List<Unit> armyUnits, List<Unit> availableUnits, int minCount) {
        if (availableUnits.isEmpty()) return;

        while (armyUnits.size() < minCount) {
            Unit cheapest = availableUnits.get(0);
            for (Unit unit : availableUnits) {
                if (unit.getCost() < cheapest.getCost()) {
                    cheapest = unit;
                }
            }
            armyUnits.add(cheapest);
        }
    }

    private double calculateEfficiency(Unit unit) {
        int cost = unit.getCost();
        if (cost == 0) return 0;

        double attackRatio = (double) unit.getBaseAttack() / cost;
        double healthRatio = (double) unit.getHealth() / cost;

        return attackRatio * 0.7 + healthRatio * 0.3;
    }

    private int calculateTotalCost(List<Unit> units) {
        int total = 0;
        for (Unit unit : units) {
            total += unit.getCost();
        }
        return total;
    }
}