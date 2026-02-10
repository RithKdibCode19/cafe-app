package com.example.backend.seeder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemSettingRepository systemSettingRepository;
    private final AddOnRepository addOnRepository;
    private final java.util.Random random = new java.util.Random();

    public DataSeeder(BranchRepository branchRepository,
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            MenuItemRepository menuItemRepository,
            IngredientRepository ingredientRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ExpenseRepository expenseRepository,
            AttendanceRepository attendanceRepository,
            StockAdjustmentRepository stockAdjustmentRepository,
            PermissionRepository permissionRepository,
            SystemSettingRepository systemSettingRepository,
            AddOnRepository addOnRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.branchRepository = branchRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.ingredientRepository = ingredientRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
        this.attendanceRepository = attendanceRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.addOnRepository = addOnRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Ensure Permissions and Settings exist (always)
        List<PermissionEntity> allPermissions = seedPermissions();
        seedSettings();
        seedAddOns();

        // 2. Check data state BEFORE ensureUserPasswords (which may create a SYSTEM branch)
        long branchCount = branchRepository.count();

        // 3. Ensure admin user and passwords
        ensureUserPasswords(allPermissions);

        if (branchCount > 4) {
             System.out.println("Data already exists. Checking for hierarchy updates...");
             migrateHierarchy();
             return;
         }

         if (branchCount > 0) {
             System.out.println("Basic data exists. SEEDING HEAVY DATA (PRO MODE)...");
             try {
                 seedHeavyData();
                 System.out.println("Heavy data seeding completed successfully!");
             } catch (Exception e) {
                 System.err.println("ERROR during heavy data seeding: " + e.getMessage());
                 e.printStackTrace();
             }
             return;
         }

        System.out.println("Seeding data...");

        // 1. Create Branch
        BranchEntity branch = new BranchEntity();
        branch.setCode("BR001");
        branch.setName("Main Street Cafe");
        branch.setLocation("123 Main St, Cityville");
        branch.setPhone("555-0101");
        branch = branchRepository.save(branch);

        // 3. Create Roles
        RoleEntity adminRole = createRole("ADMIN", "Administrator with full access", allPermissions);
        RoleEntity managerRole = createRole("MANAGER", "Store Manager", null);
        RoleEntity cashierRole = createRole("CASHIER", "Front of house stuff", null);
        RoleEntity baristaRole = createRole("BARISTA", "Coffee specialist", null);
        RoleEntity chefRole = createRole("CHEF", "Kitchen staff", null);

        // 4. Create Employees and Users
        UserEntity adminUser = findOrCreateUser(branch, "Admin User", "admin", "password", "1234", adminRole,
                "Manager",
                2500.0, 15.0);
        findOrCreateUser(branch, "John Manager", "manager", "password", "1234",
                managerRole,
                "Manager", 2000.0, 12.0);
        UserEntity cashier1 = findOrCreateUser(branch, "Alice Cashier", "alice", "password", "1234", cashierRole,
                "Cashier", 1500.0, 10.0);
        UserEntity cashier2 = findOrCreateUser(branch, "Bob Cashier", "bob", "password", "1234", cashierRole,
                "Cashier",
                1500.0, 10.0);
        UserEntity barista1 = findOrCreateUser(branch, "Charlie Barista", "charlie", "password", "1234",
                baristaRole,
                "Barista", 1600.0, 11.0);
        findOrCreateUser(branch, "David Chef", "david", "password", "1234", chefRole, "Chef", 1800.0, 12.0);

        List<UserEntity> cashiers = Arrays.asList(cashier1, cashier2);

        // 5. Create Ingredients (Inventory)
        createIngredient("Coffee Beans", "ING001", IngredientEntity.IngredientUnit.G, 500.0, 5000.0, 0.02);
        createIngredient("Whole Milk", "ING002", IngredientEntity.IngredientUnit.ML, 1000.0, 10000.0, 0.0015);
        createIngredient("Sugar", "ING003", IngredientEntity.IngredientUnit.G, 500.0, 2000.0, 0.005);
        createIngredient("Tea Leaves", "ING004", IngredientEntity.IngredientUnit.G, 200.0, 1000.0, 0.05);
        createIngredient("Flour", "ING005", IngredientEntity.IngredientUnit.G, 1000.0, 5000.0, 0.002);
        createIngredient("Eggs", "ING006", IngredientEntity.IngredientUnit.PCS, 20.0, 100.0, 0.2);
        createIngredient("Avocado", "ING007", IngredientEntity.IngredientUnit.PCS, 10.0, 50.0, 1.5);
        createIngredient("Bread", "ING008", IngredientEntity.IngredientUnit.PCS, 10.0, 40.0, 2.0);

        // 6. Create Categories and Menu Items
        CategoryEntity beverages = createCategory("Beverages", "All drinks", null);
        CategoryEntity coffeeCat = createCategory("Coffee", "Freshly brewed coffee", beverages);
        CategoryEntity teaCat = createCategory("Tea", "Premium teas", beverages);
        
        CategoryEntity food = createCategory("Food", "All food items", null);
        CategoryEntity mainDishes = createCategory("Main Dishes", "Main meals", food);
        CategoryEntity bakeryCat = createCategory("Bakery", "Fresh baked goods", food);

        List<MenuItemEntity> menuItems = new ArrayList<>();
        menuItems.add(createMenuItemWithImage(coffeeCat, "Espresso", "Strong black coffee", 2.5, "https://images.unsplash.com/photo-1579992357154-faf4bde95b3d?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(coffeeCat, "Cappuccino", "Espresso with milk foam", 3.5, "https://images.unsplash.com/photo-1534778101976-62847782c213?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(coffeeCat, "Latte", "Espresso with steamed milk", 4.0, "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(teaCat, "Green Tea", "Japanese Sencha", 3.0, "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(teaCat, "Earl Grey", "Black tea with bergamot", 3.0, "https://images.unsplash.com/photo-1597318130878-d6180fea8e6c?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(mainDishes, "Avocado Toast", "Sourdough with fresh avo", 8.5, "https://images.unsplash.com/photo-1588137378633-dea1336ce1e2?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(mainDishes, "Caesar Salad", "Classic salad", 7.0, "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(bakeryCat, "Croissant", "Buttery pastry", 2.5, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&q=80"));
        menuItems.add(createMenuItemWithImage(bakeryCat, "Muffin", "Blueberry muffin", 3.0, "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400&q=80"));


        // 7. Create Customers
        List<CustomerEntity> customers = new ArrayList<>();
        customers.add(createCustomer("Regular Ryan", "555-1001", "ryan@example.com", CustomerEntity.Gender.MALE, 100));
        customers.add(createCustomer("Loyal Lisa", "555-1002", "lisa@example.com", CustomerEntity.Gender.FEMALE, 250));
        customers.add(createCustomer("Newbie Nick", "555-1003", "nick@example.com", CustomerEntity.Gender.MALE, 0));

        // 8. Generate Past Orders (Last 30 days)
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.minusDays(i);
            int ordersCount = 5 + random.nextInt(10); // 5-15 orders per day

            for (int j = 0; j < ordersCount; j++) {
                UserEntity cashier = cashiers.get(random.nextInt(cashiers.size()));
                CustomerEntity customer = (random.nextDouble() > 0.7) ? customers.get(random.nextInt(customers.size()))
                        : null;
                createRandomOrder(branch, cashier, customer, date, menuItems);
            }

            // Generate some expenses per day
            if (random.nextDouble() > 0.8) {
                createRandomExpense(branch, adminUser, date);
            }

            // Generate attendance
            createAttendance(cashier1.getEmployee(), date);
            createAttendance(cashier2.getEmployee(), date);
            createAttendance(barista1.getEmployee(), date);
        }

        try {
            seedHeavyData();
        } catch (Exception e) {
            System.err.println("ERROR during heavy data seeding: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Data seeding completed successfully!");
    }

    private void seedHeavyData() {
        System.out.println("Seeding HEAVY data...");
        
        // Ensure initial branch exists for reference
        BranchEntity mainBranch = branchRepository.findByCode("BR001").orElse(null);
        if (mainBranch == null) return; // Should not happen if basic seed ran
        
        // 1. Create New Branches
        BranchEntity downtown = createBranch("BR002", "Downtown Cafe", "456 Urban Ave", "555-0202");
        BranchEntity mall = createBranch("BR003", "City Mall Kiosk", "789 Mall Rd (2nd Floor)", "555-0303");
        BranchEntity airport = createBranch("BR004", "Airport Terminal 3", "Terminal 3, Gate 12", "555-0404");
        BranchEntity uni = createBranch("BR005", "University Campus", "Student Center, Room 101", "555-0505");
        
        List<BranchEntity> newBranches = Arrays.asList(downtown, mall, airport, uni);
        List<BranchEntity> allBranches = new ArrayList<>();
        allBranches.add(mainBranch);
        allBranches.addAll(newBranches);
        
        // 2. Create Employees for new branches
        RoleEntity managerRole = roleRepository.findByRoleNameAndDeletedAtIsNull("MANAGER").orElseThrow();
        RoleEntity cashierRole = roleRepository.findByRoleNameAndDeletedAtIsNull("CASHIER").orElseThrow();
        RoleEntity baristaRole = roleRepository.findByRoleNameAndDeletedAtIsNull("BARISTA").orElseThrow();
        RoleEntity chefRole = roleRepository.findByRoleNameAndDeletedAtIsNull("CHEF").orElseThrow();
        
        List<UserEntity> allCashiers = new ArrayList<>();
        // Add existing cashiers
        userRepository.findAll().forEach(u -> {
            if (u.getRole().getRoleName().equals("CASHIER")) allCashiers.add(u);
        });

        System.out.println("Creating employees for new branches...");
        for (BranchEntity b : newBranches) {
            // Manager
            findOrCreateUser(b, "Manager " + b.getCode(), "mgr_" + b.getCode().toLowerCase(), "1234", "1234", managerRole, "Manager", 2200.0, 14.0);
            
            // Cashiers (2 per branch)
            allCashiers.add(findOrCreateUser(b, "Cashier 1 " + b.getCode(), "c1_" + b.getCode().toLowerCase(), "1234", "1234", cashierRole, "Cashier", 1500.0, 10.0));
            allCashiers.add(findOrCreateUser(b, "Cashier 2 " + b.getCode(), "c2_" + b.getCode().toLowerCase(), "1234", "1234", cashierRole, "Cashier", 1500.0, 10.0));
            
            // Baristas (2 per branch)
            findOrCreateUser(b, "Barista 1 " + b.getCode(), "b1_" + b.getCode().toLowerCase(), "1234", "1234", baristaRole, "Barista", 1600.0, 11.0);
            findOrCreateUser(b, "Barista 2 " + b.getCode(), "b2_" + b.getCode().toLowerCase(), "1234", "1234", baristaRole, "Barista", 1600.0, 11.0);
            
            // Kitchen
            findOrCreateUser(b, "Chef " + b.getCode(), "chef_" + b.getCode().toLowerCase(), "1234", "1234", chefRole, "Chef", 1800.0, 12.0);
        }
        System.out.println("Employees created/verified for all branches.");
        
        // 3. Create Bulk Customers
        List<CustomerEntity> customers = new ArrayList<>();
        // Fetch existing first
        customers.addAll(customerRepository.findAll());
        
        String[] firstNames = {"James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};
        
        for (int i = 0; i < 50; i++) {
             String fn = firstNames[random.nextInt(firstNames.length)];
             String ln = lastNames[random.nextInt(lastNames.length)];
             String name = fn + " " + ln;
             String email = fn.toLowerCase() + "." + ln.toLowerCase() + random.nextInt(100) + "@example.com";
             String phone = "555-" + (1000 + random.nextInt(9000));
             CustomerEntity.Gender gender = random.nextBoolean() ? CustomerEntity.Gender.MALE : CustomerEntity.Gender.FEMALE;
             int points = random.nextInt(500);
             
             if (customerRepository.findByEmail(email).isEmpty()) {
                 customers.add(createCustomer(name, phone, email, gender, points));
             }
        }
        
        // 4. Generate Historical Orders (Last 60 days)
        // Adjust volume per branch
        List<MenuItemEntity> menuItems = menuItemRepository.findAll();
        UserEntity adminUser = userRepository.findByUserNameAndDeletedAtIsNull("admin").orElse(null);

        LocalDate today = LocalDate.now();
        // Skip order generation if orders already exist (idempotent)
        long existingOrders = orderRepository.count();
        if (existingOrders > 500) {
            System.out.println("Historical orders already exist (" + existingOrders + " orders). Skipping generation.");
            return;
        }

        System.out.println("Generating historical orders (this may take a moment)...");
        int totalOrdersCreated = 0;
        
        for (int i = 0; i < 60; i++) {
            LocalDate date = today.minusDays(i);
            
            for (BranchEntity b : allBranches) {
                 // Determine daily volume based on branch type and day of week
                 int baseVolume = 10;
                 if (b.getCode().equals("BR001")) baseVolume = 20; // Main St
                 else if (b.getCode().equals("BR002")) baseVolume = 40; // Downtown (Busy)
                 else if (b.getCode().equals("BR003")) { // Mall
                     boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
                     baseVolume = isWeekend ? 60 : 15;
                 }
                 else if (b.getCode().equals("BR004")) baseVolume = 30; // Airport (Steady)
                 else if (b.getCode().equals("BR005")) baseVolume = 25; // Uni
                 
                 // Random fluctuation
                 int dailyOrders = baseVolume + random.nextInt(20) - 5;
                 if (dailyOrders < 0) dailyOrders = 5;
                 
                 // Get cashiers for THIS branch
                 List<UserEntity> branchCashiers = getCashiersForBranch(b, allCashiers);
                 if (branchCashiers.isEmpty()) continue;
                 
                 for (int j = 0; j < dailyOrders; j++) {
                     UserEntity cashier = branchCashiers.get(random.nextInt(branchCashiers.size()));
                     CustomerEntity customer = (random.nextDouble() > 0.6) ? customers.get(random.nextInt(customers.size())) : null;
                     createRandomOrder(b, cashier, customer, date, menuItems);
                     totalOrdersCreated++;
                 }
                 
                 // Generate Expenses
                  if (random.nextDouble() > 0.7 && adminUser != null) {
                    createRandomExpense(b, adminUser, date);
                }
            }
            
            if (i % 10 == 0) {
                System.out.println("  Progress: " + (i + 1) + "/60 days seeded (" + totalOrdersCreated + " orders so far)...");
            }
        }
        System.out.println("Historical order generation complete: " + totalOrdersCreated + " orders created.");
    }
    
    private BranchEntity createBranch(String code, String name, String loc, String phone) {
        return branchRepository.findByCode(code).orElseGet(() -> {
            BranchEntity b = new BranchEntity();
            b.setCode(code);
            b.setName(name);
            b.setLocation(loc);
            b.setPhone(phone);
            return branchRepository.save(b);
        });
    }
    
    private List<UserEntity> getCashiersForBranch(BranchEntity branch, List<UserEntity> allCashiers) {
        List<UserEntity> res = new ArrayList<>();
        for (UserEntity u : allCashiers) {
            if (u.getEmployee().getBranch().getBranchId().equals(branch.getBranchId())) {
                res.add(u);
            }
        }
        return res;
    }

    private List<PermissionEntity> seedPermissions() {
        List<PermissionEntity> perms = new ArrayList<>();
        // Menu
        addPermissionIfMissing(perms, "MENU_VIEW", "View menu items");
        addPermissionIfMissing(perms, "MENU_MANAGE", "Create/Edit/Delete menu items");
        // Orders
        addPermissionIfMissing(perms, "ORDER_CREATE", "Create new orders");
        addPermissionIfMissing(perms, "ORDER_VIEW", "View order history");
        addPermissionIfMissing(perms, "ORDER_VOID", "Void or refund orders");
        // Staff
        addPermissionIfMissing(perms, "STAFF_VIEW", "View staff list");
        addPermissionIfMissing(perms, "STAFF_MANAGE", "Manage employees and shifts");
        // Inventory
        addPermissionIfMissing(perms, "INVENTORY_VIEW", "View stock levels");
        addPermissionIfMissing(perms, "INVENTORY_MANAGE", "Stock in and adjustments");
        // Reports
        addPermissionIfMissing(perms, "REPORT_VIEW", "View sales reports");
        // Settings
        addPermissionIfMissing(perms, "SETTINGS_MANAGE", "Manage system settings");

        return permissionRepository.findAll();
    }

    private void addPermissionIfMissing(List<PermissionEntity> perms, String code, String description) {
        if (permissionRepository.findByCode(code).isEmpty()) {
            perms.add(permissionRepository.save(new PermissionEntity(null, code, description)));
        }
    }

    private void seedSettings() {
        createSetting("APP_NAME", "Cafe POS", "Application Name", "GENERAL");
        createSetting("TAX_RATE", "0.1", "Default Tax Rate (0.1 = 10%)", "FINANCE");
        createSetting("CURRENCY_SYMBOL", "$", "Currency Symbol", "FINANCE");
        createSetting("RECEIPT_FOOTER", "Thank you for dining with us!", "Receipt Footer Message", "GENERAL");
        createSetting("THEME", "LIGHT", "Default Theme", "APPEARANCE");
    }

    private void seedAddOns() {
        if (addOnRepository.count() > 0) return;
        
        System.out.println("Seeding add-ons...");
        
        // Coffee Add-ons
        createAddOn("Extra Shot", 0.50);
        createAddOn("Double Shot", 1.00);
        createAddOn("Vanilla Syrup", 0.50);
        createAddOn("Caramel Syrup", 0.50);
        createAddOn("Hazelnut Syrup", 0.50);
        createAddOn("Chocolate Syrup", 0.50);
        
        // Milk Options
        createAddOn("Soy Milk", 0.60);
        createAddOn("Oat Milk", 0.70);
        createAddOn("Almond Milk", 0.70);
        createAddOn("Coconut Milk", 0.60);
        createAddOn("Extra Milk", 0.30);
        
        // Toppings
        createAddOn("Whipped Cream", 0.50);
        createAddOn("Chocolate Drizzle", 0.40);
        createAddOn("Caramel Drizzle", 0.40);
        createAddOn("Cinnamon Powder", 0.20);
        createAddOn("Pearl (Boba)", 0.80);
        createAddOn("Jelly", 0.60);
        
        // Size modifiers (as add-ons)
        createAddOn("Upsize to Medium", 0.50);
        createAddOn("Upsize to Large", 1.00);
    }

    private void createAddOn(String name, Double price) {
        AddOnEntity addOn = new AddOnEntity();
        addOn.setName(name);
        addOn.setPrice(price);
        addOnRepository.save(addOn);
    }

    private void ensureUserPasswords(List<PermissionEntity> allPermissions) {
        // 1. Ensure Admin exists and has password '1234'
        Optional<UserEntity> adminOpt = userRepository.findByUserNameAndDeletedAtIsNull("admin");

        if (adminOpt.isEmpty()) {
            System.out.println("No admin user found. Creating default admin...");
            BranchEntity branch = branchRepository.findAll().stream().findFirst().orElseGet(() -> {
                BranchEntity b = new BranchEntity();
                b.setCode("SYSTEM");
                b.setName("System Branch");
                return branchRepository.save(b);
            });

            RoleEntity adminRole = roleRepository.findByRoleNameAndDeletedAtIsNull("ADMIN")
                    .orElseGet(() -> createRole("ADMIN", "Administrator", allPermissions));

            createEmployeeAndUser(branch, "Admin User", "admin", "1234", "1234", adminRole, "Admin", 0.0, 0.0);
            System.out.println("Default admin user created (admin / 1234)");
        }

        // 2. Ensure ALL users have '1234' as their password during development
        List<UserEntity> allUsers = userRepository.findAll();
        for (UserEntity user : allUsers) {
            // We force reset everything to '1234' so the user doesn't get confused
            user.setPassword(passwordEncoder.encode("1234"));
            userRepository.save(user);
            System.out.println("Forced password '1234' for user: " + user.getUserName());
        }
    }

    private void createSetting(String key, String value, String desc, String group) {
        if (systemSettingRepository.findByKey(key).isEmpty()) {
            SystemSettingEntity setting = new SystemSettingEntity();
            setting.setKey(key);
            setting.setValue(value);
            setting.setDescription(desc);
            setting.setGroup(group);
            systemSettingRepository.save(setting);
        }
    }

    private RoleEntity createRole(String name, String description, List<PermissionEntity> permissions) {
        return roleRepository.findByRoleNameAndDeletedAtIsNull(name).orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setRoleName(name);
            role.setDescription(description);
            if (permissions != null) {
                role.setPermissions(new java.util.HashSet<>(permissions));
            }
            return roleRepository.save(role);
        });
    }

    private UserEntity createEmployeeAndUser(BranchEntity branch, String name, String username, String password,
            String pin,
            RoleEntity role, String position, Double salary, Double hourly) {
        EmployeeEntity emp = new EmployeeEntity();
        emp.setFullName(name);
        emp.setBranch(branch);
        emp.setPosition(position);
        emp.setBaseSalary(salary);
        emp.setHourlyRate(hourly);
        emp.setStatus(EmployeeEntity.Status.ACTIVE);
        employeeRepository.save(emp);

        UserEntity user = new UserEntity();
        user.setEmployee(emp);
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPinCode(pin);
        user.setRole(role);
        return userRepository.save(user);
    }

    private UserEntity findOrCreateUser(BranchEntity branch, String name, String username, String password,
            String pin, RoleEntity role, String position, Double salary, Double hourly) {
        Optional<UserEntity> existing = userRepository.findByUserNameAndDeletedAtIsNull(username);
        if (existing.isPresent()) {
            return existing.get();
        }
        return createEmployeeAndUser(branch, name, username, password, pin, role, position, salary, hourly);
    }

    private IngredientEntity createIngredient(String name, String sku, IngredientEntity.IngredientUnit unit,
            Double reorderLevel, Double stock, Double cost) {
        IngredientEntity ing = new IngredientEntity();
        ing.setName(name);
        ing.setSku(sku);
        ing.setUnit(unit);
        ing.setReorderLevel(reorderLevel);
        ing.setCurrentStock(stock);
        ing.setCostPerUnit(cost);
        return ingredientRepository.save(ing);
    }

    private CategoryEntity createCategory(String name, String desc, CategoryEntity parent) {
        CategoryEntity cat = new CategoryEntity();
        cat.setName(name);
        cat.setDescription(desc);
        cat.setParent(parent);
        return categoryRepository.save(cat);
    }

    private MenuItemEntity createMenuItem(CategoryEntity cat, String name, String desc, Double price) {
        MenuItemEntity item = new MenuItemEntity();
        item.setCategory(cat);
        item.setName(name);
        item.setBasePrice(price);
        item.setIsAvailable(true);
        item.setImageUrl("https://placehold.co/200?text=" + name.replace(" ", "+"));
        return menuItemRepository.save(item);
    }

    private MenuItemEntity createMenuItemWithImage(CategoryEntity cat, String name, String desc, Double price, String imageUrl) {
        MenuItemEntity item = new MenuItemEntity();
        item.setCategory(cat);
        item.setName(name);
        item.setBasePrice(price);
        item.setIsAvailable(true);
        item.setImageUrl(imageUrl);
        return menuItemRepository.save(item);
    }


    private CustomerEntity createCustomer(String name, String phone, String email, CustomerEntity.Gender gender,
            Integer points) {
        CustomerEntity cust = new CustomerEntity();
        cust.setName(name);
        cust.setPhone(phone);
        cust.setEmail(email);
        cust.setGender(gender);
        cust.setLoyaltyPoints(points);
        cust.setStatus(CustomerEntity.Status.ACTIVE);
        cust.setMembershipLevel(points > 200 ? "GOLD" : "BRONZE");
        return customerRepository.save(cust);
    }

    private void createRandomOrder(BranchEntity branch, UserEntity cashier, CustomerEntity customer, LocalDate date,
            List<MenuItemEntity> menuItems) {
        OrderEntity order = new OrderEntity();
        order.setBranch(branch);
        order.setCashierUser(cashier);
        order.setCustomer(customer);
        order.setOrderNo("ORD-" + date.toString().replace("-", "") + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setOrderType(OrderEntity.OrderType.DINE_IN);
        order.setStatus(OrderEntity.OrderStatus.PAID);

        // Random time between 8am and 8pm
        LocalTime time = LocalTime.of(8 + random.nextInt(12), random.nextInt(60));
        order.setCreatedAt(LocalDateTime.of(date, time));

        List<OrderItemEntity> items = new ArrayList<>();
        double subtotal = 0;
        int itemCount = 1 + random.nextInt(4);

        for (int i = 0; i < itemCount; i++) {
            MenuItemEntity menuItem = menuItems.get(random.nextInt(menuItems.size()));
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQty(1 + random.nextInt(2));
            orderItem.setUnitPrice(menuItem.getBasePrice());
            items.add(orderItem);
            subtotal += (orderItem.getQty() * orderItem.getUnitPrice());
        }

        order.setItems(items);
        order.setSubTotal(subtotal);
        double tax = subtotal * 0.1;
        order.setTaxAmount(tax);
        order.setTotalAmount(subtotal + tax);

        order = orderRepository.save(order);

        // Payment
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setPaidAmount(order.getTotalAmount());
        payment.setChangeAmount(0.0);
        payment.setPaymentStatus(PaymentEntity.PaymentStatus.PAID);
        payment.setMethod(PaymentEntity.PaymentMethod.CASH);
        payment.setCreatedAt(order.getCreatedAt());
        paymentRepository.save(payment);
    }

    private void createRandomExpense(BranchEntity branch, UserEntity user, LocalDate date) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setBranch(branch);
        expense.setRecordedBy(user);
        expense.setDate(date);
        expense.setAmount(50.0 + random.nextDouble() * 200.0);

        List<String> types = Arrays.asList("Urgent Supplies", "Maintenance", "Office Supplies");
        expense.setTitle(types.get(random.nextInt(types.size())));
        expense.setCategory("Operational");
        expense.setDescription("Generated expense");

        expenseRepository.save(expense);
    }

    private void createAttendance(EmployeeEntity emp, LocalDate date) {
        // Skip some days randomly
        if (random.nextDouble() > 0.9)
            return;

        AttendanceEntity att = new AttendanceEntity();
        att.setEmployee(emp);
        att.setCheckIn(LocalDateTime.of(date, LocalTime.of(8, random.nextInt(15))));
        att.setCheckOut(LocalDateTime.of(date, LocalTime.of(17, random.nextInt(30))));
        att.setStatus(AttendanceEntity.AttendanceStatus.PRESENT);
        attendanceRepository.save(att);
        att.setStatus(AttendanceEntity.AttendanceStatus.PRESENT);
        attendanceRepository.save(att);
    }

    private void migrateHierarchy() {
        // 1. Beverages Structure
        CategoryEntity beverages = getOrCreateCategory("Beverages", "All drinks", null);
        
        // Coffee branch
        CategoryEntity coffee = getOrCreateCategory("Coffee", "Freshly brewed coffee", beverages);
        CategoryEntity hotCoffee = getOrCreateCategory("Hot Coffee", "Steaming hot coffee", coffee);
        CategoryEntity icedCoffee = getOrCreateCategory("Iced Coffee", "Cold and refreshing", coffee);
        CategoryEntity frappeCoffee = getOrCreateCategory("Frappe", "Blended ice coffee", coffee);

        // Tea branch
        CategoryEntity tea = getOrCreateCategory("Tea & Milk", "Tea and milk based drinks", beverages);
        CategoryEntity icedTea = getOrCreateCategory("Iced Tea", "Cold tea drinks", tea);
        CategoryEntity frappeTea = getOrCreateCategory("Tea Frappe", "Blended tea", tea);
        CategoryEntity hotTea = getOrCreateCategory("Hot Tea", "Hot tea", tea);

        // Food branch
        CategoryEntity food = getOrCreateCategory("Food", "All food items", null);
        CategoryEntity bakery = getOrCreateCategory("Bakery", "Fresh baked goods", food);
        CategoryEntity cakes = getOrCreateCategory("Cakes", "Delicious cakes", food);

        // 2. Migrate existing simple categories if they exist to this structure
        moveCategory("Coffee", beverages); // Older migration might have put it here, but we want it specific? 
        // Actually, let's just seed new items into these specific new categories.
        
        // Seed Items (Idempotent - check by name)
        seedAmazonItems(hotCoffee, icedCoffee, frappeCoffee, icedTea, frappeTea, bakery, cakes);
    }

    private CategoryEntity getOrCreateCategory(String name, String desc, CategoryEntity parent) {
        return categoryRepository.findByName(name).orElseGet(() -> {
            System.out.println("Creating category: " + name);
            return createCategory(name, desc, parent);
        });
    }

    private void moveCategory(String name, CategoryEntity newParent) {
        categoryRepository.findByName(name).ifPresent(c -> {
            if (c.getParent() == null) { // Only move if it's a root
                c.setParent(newParent);
                categoryRepository.save(c);
            }
        });
    }
    
    private void seedAmazonItems(CategoryEntity hot, CategoryEntity iced, CategoryEntity frappe, 
                               CategoryEntity icedTea, CategoryEntity frappeTea, 
                               CategoryEntity bakery, CategoryEntity cakes) {
        
        // Hot Coffee
        createItemIfNotExists(hot, "Hot Espresso", 35.0, "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=400&q=80");
        createItemIfNotExists(hot, "Hot Americano", 45.0, "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400&q=80");
        createItemIfNotExists(hot, "Hot Latte", 55.0, "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=400&q=80");
        createItemIfNotExists(hot, "Hot Cappuccino", 55.0, "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400&q=80");
        createItemIfNotExists(hot, "Hot Mocha", 60.0, "https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=400&q=80");
        createItemIfNotExists(hot, "Hot Caramel Macchiato", 70.0, "https://images.unsplash.com/photo-1485808191679-5f8c7c8606af?w=400&q=80");

        // Iced Coffee
        createItemIfNotExists(iced, "Iced Espresso", 50.0, "https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=400&q=80");
        createItemIfNotExists(iced, "Amazon Extra", 75.0, "https://images.unsplash.com/photo-1497935586351-b67a49e012bf?w=400&q=80");
        createItemIfNotExists(iced, "Iced Americano", 60.0, "https://images.unsplash.com/photo-1517701604599-bb29b5c7dd00?w=400&q=80");
        createItemIfNotExists(iced, "Iced Latte", 65.0, "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&q=80");
        createItemIfNotExists(iced, "Iced Mocha", 70.0, "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=400&q=80");
        createItemIfNotExists(iced, "Black Coffee Honey Lemon", 70.0, "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&q=80");

        // Frappe
        createItemIfNotExists(frappe, "Mocha Frappe", 80.0, "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400&q=80");
        createItemIfNotExists(frappe, "Coffee Frappe", 70.0, "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&q=80");
        createItemIfNotExists(frappe, "Double Choco Frappe", 85.0, "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400&q=80");

        // Tea
        createItemIfNotExists(icedTea, "Iced Green Tea Latte", 65.0, "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&q=80");
        createItemIfNotExists(icedTea, "Iced Thai Tea", 60.0, "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=400&q=80");
        createItemIfNotExists(icedTea, "Iced Lemon Tea", 55.0, "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&q=80");
        createItemIfNotExists(frappeTea, "Green Tea Frappe", 75.0, "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400&q=80");
        createItemIfNotExists(frappeTea, "Thai Tea Frappe", 70.0, "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=400&q=80");

        // Bakery
        createItemIfNotExists(bakery, "Chocolate Cake", 80.0, "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&q=80");
        createItemIfNotExists(bakery, "Crepes", 60.0, "https://images.unsplash.com/photo-1519676867240-f03562e64548?w=400&q=80");
        createItemIfNotExists(cakes, "Blueberry Cheesecake", 90.0, "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400&q=80");
    }

    private void createItemIfNotExists(CategoryEntity cat, String name, Double price, String img) {
         if (menuItemRepository.count() < 100) { // Simple guard to not overfill if running multiple times
            // Check existence by name might suffice for seed
             // For now just add if not super many items
             MenuItemEntity item = new MenuItemEntity();
             item.setCategory(cat);
             item.setName(name);
             item.setBasePrice(price);
             item.setIsAvailable(true);
             item.setImageUrl(img);
             menuItemRepository.save(item);
         }
    }
}
