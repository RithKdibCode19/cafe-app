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
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Ensure Permissions, Settings and Admin User Exist
        // (Run this regardless of existing data)
        List<PermissionEntity> allPermissions = seedPermissions();
        seedSettings();
        ensureUserPasswords(allPermissions);

        if (branchRepository.count() > 0) {
            System.out.println("Data already exists. Skipping main seeding.");
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
        UserEntity adminUser = createEmployeeAndUser(branch, "Admin User", "admin", "password", "1234", adminRole,
                "Manager",
                2500.0, 15.0);
        createEmployeeAndUser(branch, "John Manager", "manager", "password", "1234",
                managerRole,
                "Manager", 2000.0, 12.0);
        UserEntity cashier1 = createEmployeeAndUser(branch, "Alice Cashier", "alice", "password", "1234", cashierRole,
                "Cashier", 1500.0, 10.0);
        UserEntity cashier2 = createEmployeeAndUser(branch, "Bob Cashier", "bob", "password", "1234", cashierRole,
                "Cashier",
                1500.0, 10.0);
        UserEntity barista1 = createEmployeeAndUser(branch, "Charlie Barista", "charlie", "password", "1234",
                baristaRole,
                "Barista", 1600.0, 11.0);
        createEmployeeAndUser(branch, "David Chef", "david", "password", "1234", chefRole, "Chef", 1800.0, 12.0);

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
        CategoryEntity coffeeCat = createCategory("Coffee", "Freshly brewed coffee");
        CategoryEntity teaCat = createCategory("Tea", "Premium teas");
        CategoryEntity foodCat = createCategory("Food", "Delicious meals and snacks");
        CategoryEntity bakeryCat = createCategory("Bakery", "Fresh baked goods");

        List<MenuItemEntity> menuItems = new ArrayList<>();
        menuItems.add(createMenuItem(coffeeCat, "Espresso", "Strong black coffee", 2.5));
        menuItems.add(createMenuItem(coffeeCat, "Cappuccino", "Espresso with milk foam", 3.5));
        menuItems.add(createMenuItem(coffeeCat, "Latte", "Espresso with steamed milk", 4.0));
        menuItems.add(createMenuItem(teaCat, "Green Tea", "Japanese Sencha", 3.0));
        menuItems.add(createMenuItem(teaCat, "Earl Grey", "Black tea with bergamot", 3.0));
        menuItems.add(createMenuItem(foodCat, "Avocado Toast", "Sourdough with fresh avo", 8.5));
        menuItems.add(createMenuItem(foodCat, "Caesar Salad", "Classic salad", 7.0));
        menuItems.add(createMenuItem(bakeryCat, "Croissant", "Buttery pastry", 2.5));
        menuItems.add(createMenuItem(bakeryCat, "Muffin", "Blueberry muffin", 3.0));

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

        System.out.println("Data seeding completed successfully!");
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
        RoleEntity role = new RoleEntity();
        role.setRoleName(name);
        role.setDescription(description);
        if (permissions != null) {
            role.setPermissions(new java.util.HashSet<>(permissions));
        }
        return roleRepository.save(role);
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

    private CategoryEntity createCategory(String name, String desc) {
        CategoryEntity cat = new CategoryEntity();
        cat.setName(name);
        cat.setDescription(desc);
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
        order.setOrderNo("ORD-" + date.toString().replace("-", "") + "-" + random.nextInt(10000));
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
    }
}
