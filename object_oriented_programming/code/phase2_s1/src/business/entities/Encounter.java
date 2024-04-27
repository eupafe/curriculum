package business.entities;

import java.util.ArrayList;
import java.util.List;

public class Encounter {

    private List<Monster> monsters;
    private List<Integer> indices;

    public Encounter(){
        this.monsters = new ArrayList<Monster>();
        this.indices = new ArrayList<Integer>();

    }

    public int getNumberDifferentMonsters(){
        return this.monsters.size();
    }
    public List<Monster> getMonsters(){
        return this.monsters;
    }

    public Monster getMonster(int index){
        return this.monsters.get(index);
    }

    public int getAmountMonster(int index){
        return this.indices.get(index);
    }
    public void addMonster(Monster monster, int amount){

        this.monsters.add(monster);
        this.indices.add(amount);
    }

    public void addMonsterToList(Monster monster){
        this.monsters.add(monster);
    }

    public void addIndexToList(int amount){
        this.indices.add(amount);
    }

    public void deleteMonster(int indexMonster){
        this.monsters.remove(indexMonster);
        this.indices.remove(indexMonster);
    }

    public void updateAmountMonster(int index, int amount){
        int number = this.indices.get(index);
        number += amount;
        this.indices.set(index, number);
    }

    public String toString(){
        String hey = "";
        for (int i = 0; i < this.monsters.size(); i++) {
            hey += this.monsters.get(i);
        }
        if (this.indices.size() != 0) {
            hey += "llista: {";
        }
        for (int i = 0; i < this.indices.size(); i++) {
            hey += this.indices.get(i);
            hey += "-";
        }
        hey +="}";

        return hey;
    }


}
