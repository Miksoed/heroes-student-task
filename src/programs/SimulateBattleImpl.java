package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) {
            return;
        }

        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        List<Unit> alivePlayerUnits = getAliveUnits(playerUnits);
        List<Unit> aliveComputerUnits = getAliveUnits(computerUnits);

        int round = 1;

        while (!alivePlayerUnits.isEmpty() && !aliveComputerUnits.isEmpty()) {
            List<Unit> allUnitsThisRound = new ArrayList<>();
            allUnitsThisRound.addAll(alivePlayerUnits);
            allUnitsThisRound.addAll(aliveComputerUnits);

            allUnitsThisRound.sort((u1, u2) ->
                    Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

            List<Unit> turnQueue = new ArrayList<>(allUnitsThisRound);

            for (Unit attacker : turnQueue) {
                if (!attacker.isAlive()) {
                    continue;
                }

                Unit target = null;
                try {
                    if (attacker.getProgram() != null) {
                        target = attacker.getProgram().attack();
                    }
                } catch (Exception e) {
                }

                if (target == null) {
                    target = findFallbackTarget(attacker, alivePlayerUnits, aliveComputerUnits);
                }

                if (target != null && target.isAlive()) {
                    performAttack(attacker, target);

                    if (printBattleLog != null) {
                        printBattleLog.printBattleLog(attacker, target);
                    }

                    if (!target.isAlive()) {
                        if (alivePlayerUnits.contains(target)) {
                            alivePlayerUnits.remove(target);
                        } else {
                            aliveComputerUnits.remove(target);
                        }
                    }
                }

                if (alivePlayerUnits.isEmpty() || aliveComputerUnits.isEmpty()) {
                    break;
                }
            }

            alivePlayerUnits = getAliveUnits(alivePlayerUnits);
            aliveComputerUnits = getAliveUnits(aliveComputerUnits);

            round++;
            Thread.sleep(10); // небольшая пауза
        }
    }

    private List<Unit> getAliveUnits(List<Unit> units) {
        List<Unit> alive = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.isAlive()) {
                alive.add(unit);
            }
        }
        return alive;
    }

    private Unit findFallbackTarget(Unit attacker, List<Unit> playerUnits, List<Unit> computerUnits) {
        boolean isPlayerAttacker = playerUnits.contains(attacker);
        List<Unit> enemyUnits = isPlayerAttacker ? computerUnits : playerUnits;

        if (enemyUnits.isEmpty()) {
            return null;
        }

        for (Unit enemy : enemyUnits) {
            if (enemy.isAlive()) {
                return enemy;
            }
        }

        return null;
    }

    private void performAttack(Unit attacker, Unit target) {
        int damage = attacker.getBaseAttack();
        int currentHealth = target.getHealth();
        int newHealth = currentHealth - damage;

        if (newHealth <= 0) {
            target.setHealth(0);
            target.setAlive(false);
        } else {
            target.setHealth(newHealth);
        }
    }
}