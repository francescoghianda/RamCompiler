package main;

import java.util.HashMap;

public class Compiler
{
    public final int FORCE_STOP = -1;
    public final int ERR_NOT_TERMINATE = 1;
    public final int ERR_SYNTAX = 2;
    public final int ERR_NOT_RECOGNIZED = 3;
    public final int ERR_LABEL_NOT_DEFINED = 4;
    public final int ERR_DIV_BY_ZERO = 5;

    private RAM ram;
    private RamCompiler rc;

    private Status status;

    private HashMap<String, Integer> labels;
    private HashMap<String, Integer> preLabels;
    private String[] lines;

    private boolean forceStop;

    private boolean debugMode;
    private boolean execNext;

    public Compiler(RAM ram, RamCompiler rc)
    {
        this.ram = ram;
        this.rc = rc;
        this.labels = new HashMap<>();
        this.preLabels = new HashMap<>();
        this.status = new Status(0, 0, "");
    }

    public Status setProgram(String program)
    {
        Status e;
        String[] lines = program.split("\n");
        labels.clear();
        preLabels.clear();
        ram.reset();
        if((e = preprocess(lines)).getStatus() != 0)return e;
        this.lines = lines;
        return status.set(0, 0, "");
    }


    public Status run()
    {
        boolean jumped;
        boolean jump;

        for(int i = 0; i < lines.length; i++)
        {
            if(forceStop)break;
            rc.setProgramCounterLabel(i+1);
            if(lines[i].trim().equals(""))continue;

            if(debugMode)
            {
                while(!execNext) try
                {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String[] tokens = lines[i].split(" ");
            if(tokens[0].equals("HALT"))
            {
                rc.setLastInstruction(tokens[0]);
                break;
            }
            jumped = false;
            jump = false;
            switch(tokens[0])
            {
                case "LOAD":
                    ram.load(Integer.parseInt(tokens[1]));
                    break;
                case "LOAD=":
                    ram.directLoad(Integer.parseInt(tokens[1]));
                    break;
                case "LOAD*":
                    ram.pointerLoad(Integer.parseInt(tokens[1]));
                    break;
                case "STORE":
                    ram.store(Integer.parseInt(tokens[1]));
                    break;
                case "STORE*":
                    ram.pointerStore(Integer.parseInt(tokens[1]));
                    break;
                case "ADD":
                    ram.add(Integer.parseInt(tokens[1]));
                    break;
                case "ADD=":
                    ram.directAdd(Integer.parseInt(tokens[1]));
                    break;
                case "SUB":
                    ram.sub(Integer.parseInt(tokens[1]));
                    break;
                case "SUB=":
                    ram.directSub(Integer.parseInt(tokens[1]));
                    break;
                case "MULT":
                    ram.mult(Integer.parseInt(tokens[1]));
                    break;
                case "MULT=":
                    ram.directMult(Integer.parseInt(tokens[1]));
                    break;
                case "DIV":
                    if(ram.getCell(Integer.parseInt(tokens[1])) == 0)return status.set(ERR_DIV_BY_ZERO, i, "Divisione per 0 impossibile!");
                    ram.div(Integer.parseInt(tokens[1]));
                    break;
                case "DIV=":
                    if(Integer.parseInt(tokens[1]) == 0)return status.set(ERR_DIV_BY_ZERO, i, "Divisione per 0 impossibile!");
                    ram.directDiv(Integer.parseInt(tokens[1]));
                    break;
                case "JUMP":
                    //if(!labels.containsKey(tokens[1]))return status.set(ERR_LABEL_NOT_DEFINED, i, "Etichetta <"+tokens[1]+"> non definita!");
                    i = labels.get(tokens[1])-1;
                    jump = true;
                    jumped = true;
                    break;
                case "JGZ":
                    jump = true;
                    if(!(ram.getCell(0) > 0))break;
                    //if(!labels.containsKey(tokens[1]))return status.set(ERR_LABEL_NOT_DEFINED, i, "Etichetta <"+tokens[1]+"> non definita!");
                    i = labels.get(tokens[1])-1;
                    jumped = true;
                    break;
                case "JZ":
                    jump = true;
                    if(ram.getCell(0) != 0)break;
                    //if(!labels.containsKey(tokens[1]))return status.set(ERR_LABEL_NOT_DEFINED, i, "Etichetta <"+tokens[1]+"> non definita!");
                    i = labels.get(tokens[1])-1;
                    jumped = true;
                    break;
                case "READ":
                    ram.setCell(Integer.parseInt(tokens[1]), rc.read(i+1, Integer.parseInt(tokens[1])));
                    break;
                case "READ*":
                    ram.setCell(ram.getCell(Integer.parseInt(tokens[1])), rc.read(i+1, ram.getCell(Integer.parseInt(tokens[1]))));
                    break;
                case "WRITE":
                    rc.write(ram.getCell(Integer.parseInt(tokens[1])));
                    break;
                case "WRITE*":
                    rc.write(ram.getCell(ram.getCell(Integer.parseInt(tokens[1]))));
                    break;
                case "WRITE=":
                    rc.write(Integer.parseInt(tokens[1]));
                    break;
            }
            if(jump)rc.lastJump(tokens[0]+" "+tokens[1]+" -> "+String.valueOf(jumped));
            rc.setLastInstruction(tokens[0]+" "+tokens[1]);
            rc.updateTable();
            execNext = false;
        }

        if(forceStop)
        {
            forceStop = false;
            return status.set(FORCE_STOP, -1, "Programma interrotto");
        }

        return status.set(0, 0, "");
    }
    
    private Status preprocess(String[] lines)
    {
        //if(lines.length != 0)if(!lines[lines.length-1].contains("HALT"))return status.set(ERR_NOT_TERMINATE, lines.length-1, "Istruzione <HALT> mancante!");//??
        for(int i = 0; i < lines.length; i++)
        {
            if(forceStop)break;
            lines[i] = lines[i].trim();
            if(lines[i].trim().equals(""))continue;
            String[] tokens = lines[i].split(" ");
            if(tokens.length > 3)return status.set(ERR_SYNTAX, i, "Errore di sintassi!");
            else if(tokens.length == 3)
            {
                if(!tokens[0].endsWith(":"))return status.set(ERR_SYNTAX, i, "Errore di sintassi!\nManca ':' dopo la definizione dell'etichetta");
                labels.put(tokens[0].substring(0, tokens[0].length()-1), i);
                lines[i] = tokens[1]+" "+tokens[2];
                i--;
                continue;
            }
            else if(tokens.length == 2)
            {
                if(isInstruction(tokens[0]) && isPositiveInteger(tokens[1]))continue;
                if(isJump(tokens[0]))
                {
                    if(tokens[1].equals(""))return status.set(ERR_SYNTAX, i, "Errore di sintassi!\nManca l'etichetta dopo l'istruzione <"+tokens[0]+">");
                    if(labels.containsKey(tokens[1]))continue;
                    preLabels.put(tokens[1], i);
                    continue;
                }
            }
            else
            {
                if(tokens[0].equals("HALT"))continue;
                if(tokens[0].endsWith(":"))
                {
                    labels.put(tokens[0].substring(0, tokens[0].length()-1), i);
                    lines[i] = "";
                    continue;
                }
            }

            return status.set(ERR_NOT_RECOGNIZED, i, "Istruzione <"+tokens[0]+"> non riconosciuta!");
        }

        for(int i = 0; i < preLabels.size(); i++)
        {
            if(forceStop)break;
            if(!labels.containsKey(preLabels.keySet().toArray()[i]))return status.set(ERR_LABEL_NOT_DEFINED, preLabels.get(preLabels.keySet().toArray()[i]), "Etichetta <"+preLabels.keySet().toArray()[i]+"> non definita!");
        }

        if(forceStop)
        {
            forceStop = false;
            return status.set(FORCE_STOP, -1, "Programma interrotto");
        }

        return status.set(0, 0, "");
    }

    private boolean isInstruction(String str)
    {
        if(str.equals("LOAD") || str.equals("LOAD=") || str.equals("LOAD*")
                || str.equals("STORE") || str.equals("STORE*")
                || str.equals("ADD") || str.equals("SUB") || str.equals("DIV") || str.equals("MULT")
                || str.equals("ADD=") || str.equals("SUB=") || str.equals("DIV=") || str.equals("MULT=")
                || str.equals("READ") || str.equals("READ*")
                || str.equals("WRITE") || str.equals("WRITE=") || str.equals("WRITE*"))return true;
        return false;
    }

    private boolean isJump(String str)
    {
        if(str.equals("JUMP") || str.equals("JZ") || str.equals("JGZ"))return true;
        return false;
    }

    private boolean isPositiveInteger(String str)
    {
        return str.matches("\\d+(\\.\\d+)?");
    }


    public void stop()
    {
        this.forceStop = true;
    }

    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
        this.execNext = false;
    }

    public boolean isDebugMode()
    {
        return this.debugMode;
    }

    public void execNextInstruction()
    {
        this.execNext = true;
    }

}
