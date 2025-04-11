import java.util.*;

class Memory {
    // Runtime Input
    public static Scanner data;

    // GC & Reference Tracking
    public static int reachableObj = 0;
    public static IdentityHashMap<Map<String, Integer>, Integer> refs = new IdentityHashMap<>();

    // Variable Representation
    static class Variable {
        Core core;
        int integer;
        String string;
        Map<String, Integer> mapVal;

        Variable(Core core) {
            this.core = core;
        }
    }

    // Memory Storage Structures
    public static HashMap<String, Variable> global;
    public static Stack<Stack<HashMap<String, Variable>>> local;
    public static HashMap<String, Function> funcMap;

    // Shows the current number of reachable objects.
    private static void reachableObjs() {
        System.out.println("gc:" + reachableObj);
    }

    // Adds a reference to an object and updates GC if needed.
    private static void addRef(Map<String, Integer> map) {
		if (map != null) {
			int i = refs.getOrDefault(map, 0);
			refs.put(map, i + 1);
			if (i == 0) {
				reachableObj++;
				reachableObjs();
			}
		}
		return;
    }

    // Removes a reference and updates GC if the object is no longer used.
    private static void removeRef(Map<String, Integer> map) {
		boolean exit = (map == null || refs.getOrDefault(map, 0) <= 0);
		if (!exit) {
			int i = refs.get(map) - 1;
			if (i == 0) {
				refs.remove(map);
				reachableObj--;
				reachableObjs();
			} else {
				refs.put(map, i);
			}
		}
		return;
    }

    // Sets up empty global variables and functions.
    public static void initializeGlobal() {
        global = new HashMap<>();
        funcMap = new HashMap<>();
    }

    // Clears all global variables and removes object references.
    public static void clearGlobal() {
        for (Variable v : global.values()) {
            if (v.core == Core.OBJECT && v.mapVal != null) {
                removeRef(v.mapVal);
                v.mapVal = null;
            }
        }
        global = null;
        funcMap = null;
    }

    // Starts the local memory stack with an empty scope.
    public static void initializeLocal() {
        local = new Stack<>();
        local.push(new Stack<>());
        local.peek().push(new HashMap<>());
    }

    // Adds a new scope to the current stack frame (e.g., for if/loop).
    public static void pushScope() {
        local.peek().push(new HashMap<>());
    }

    // Removes the current scope and releases any objects in it.
    public static void popScope() {
        HashMap<String, Variable> scopeMap = local.peek().pop();
        for (Variable v : scopeMap.values()) {
            if (v.core == Core.OBJECT && v.mapVal != null) {
                removeRef(v.mapVal);
                v.mapVal = null;
            }
        }
    }

    // Declares a new integer variable in the current scope.
    public static void declareInteger(String id) {
        Variable v = new Variable(Core.INTEGER);
        if (local != null) {
            local.peek().peek().put(id, v);
        } else {
            global.put(id, v);
        }
    }

    // Declares a new object variable in the current scope.
    public static void declareObject(String id) {
        Variable v = new Variable(Core.OBJECT);
        if (local != null) {
            local.peek().peek().put(id, v);
        } else {
            global.put(id, v);
        }
    }

    // Gets the value of an integer or the default key of an object.
    public static int load(String id) {
        Variable v = getLocalOrGlobal(id);
        return (v.core == Core.INTEGER) ? v.integer : v.mapVal.get(v.string);
    }

    // Gets the value from an object using a given key.
    public static int load(String id, String key) {
        return getLocalOrGlobal(id).mapVal.get(key);
    }

    // Stores a value into an integer or an object’s default key.
    public static void store(String id, int value) {
        Variable v = getLocalOrGlobal(id);
        if (v.core == Core.INTEGER) {
            v.integer = value;
        } else {
            v.mapVal.put(v.string, value);
        }
    }

    // Stores a value into an object using a custom key.
    public static void store(String id, String key, int value) {
        getLocalOrGlobal(id).mapVal.put(key, value);
    }

    // Creates a new object and sets its default key and value.
    public static void allocate(String id, String key, int value) {
        Variable v = getLocalOrGlobal(id);
        if (v.mapVal != null) {
            removeRef(v.mapVal);
        }
        v.mapVal = new HashMap<>();
        v.string = key;
        v.mapVal.put(key, value);
        addRef(v.mapVal);
    }

    // Makes one object variable point to the same object as another.
    public static void alias(String lhs, String rhs) {
        Variable v1 = getLocalOrGlobal(lhs);
        Variable v2 = getLocalOrGlobal(rhs);
        if (v1.mapVal != null) {
            removeRef(v1.mapVal);
        }
        v1.mapVal = v2.mapVal;
        v1.string = v2.string;
        if (v1.mapVal != null) {
            addRef(v1.mapVal);
        }
    }

    // Searches for a variable name starting from the current scope and moving out.
    private static Variable getLocalOrGlobal(String id) {
        if (local.peek().size() > 0) {
            if (local.peek().peek().containsKey(id)) {
                return local.peek().peek().get(id);
            } else {
                HashMap<String, Variable> temp = local.peek().pop();
                Variable result = getLocalOrGlobal(id);
                local.peek().push(temp);
                return result;
            }
        }
        return global.get(id);
    }

    // Sets up a new frame with passed-in arguments and runs the function.
    public static void pushFrameAndExecute(String name, Parameter args) {
        Function f = funcMap.get(name);
        ArrayList<String> formals = f.param.execute();
        ArrayList<String> arguments = args.execute();

        Stack<HashMap<String, Variable>> frame = new Stack<>();
        frame.push(new HashMap<>());

        for (int i = 0; i < arguments.size(); i++) {
            Variable argVar = getLocalOrGlobal(arguments.get(i));
            Variable paramVar = new Variable(Core.OBJECT);
            paramVar.mapVal = argVar.mapVal;
            paramVar.string = argVar.string;

            if (paramVar.mapVal != null) {
                addRef(paramVar.mapVal);
            }

            frame.peek().put(formals.get(i), paramVar);
        }

        local.push(frame);
        f.ss.execute();
    }

    // Cleans up the current frame and removes object references.
    public static void popFrame() {
        Stack<HashMap<String, Variable>> frame = local.pop();
        while (!frame.isEmpty()) {
            HashMap<String, Variable> scopeMap = frame.pop();
            for (Variable v : scopeMap.values()) {
                if (v.core == Core.OBJECT && v.mapVal != null) {
                    removeRef(v.mapVal);
                    v.mapVal = null;
                }
            }
        }
    }
}