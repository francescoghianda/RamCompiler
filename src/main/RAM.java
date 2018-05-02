package main;

import java.util.ArrayList;

public class RAM
{

    private ArrayList<Integer> mem;

    public RAM()
    {
        mem = new ArrayList<>();
        mem.add(0);
    }

    private void allocate(int size)
    {
        for(int i = mem.size()-1; i < size; i++)
        {
            mem.add(0);
        }
    }

    public void load(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(0, mem.get(address));
    }

    public void directLoad(int value)
    {
        mem.set(0, value);
    }

    public void pointerLoad(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        if(mem.size()-1 < mem.get(address))allocate(mem.get(address));
        mem.set(0, mem.get(mem.get(address)));
    }

    public void store(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(address, mem.get(0));
    }

    public void pointerStore(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        if(mem.size()-1 < mem.get(address))allocate(mem.get(address));
        mem.set(mem.get(address), mem.get(0));
    }

    public void add(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(0, mem.get(0)+mem.get(address));
    }

    public void directAdd(int value)
    {
        mem.set(0, mem.get(0)+value);
    }

    public void sub(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(0, mem.get(0)-mem.get(address));
    }

    public void directSub(int value)
    {
        mem.set(0, mem.get(0)-value);
    }

    public void mult(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(0, mem.get(0)*mem.get(address));
    }

    public void directMult(int value)
    {
        mem.set(0, mem.get(0)*value);
    }

    public void div(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(0, mem.get(0)/mem.get(address));
    }

    public void directDiv(int value)
    {
        mem.set(0, mem.get(0)/value);
    }

    public int getCell(int address)
    {
        if(mem.size()-1 < address)allocate(address);
        return mem.get(address);
    }

    public void setCell(int address, int value)
    {
        if(mem.size()-1 < address)allocate(address);
        mem.set(address, value);
    }

    public ArrayList getMem()
    {
        return mem;
    }

    public void reset()
    {
        mem.clear();
        mem.add(0);
    }

}
