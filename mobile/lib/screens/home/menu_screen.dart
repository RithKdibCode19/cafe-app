import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:shimmer/shimmer.dart';
import '../../config/theme.dart';
import '../../models/menu_item.dart';
import '../../providers/menu_provider.dart';
import '../../providers/cart_provider.dart';
import '../../providers/auth_provider.dart';
import '../home/branch_select_screen.dart';

class MenuScreen extends StatefulWidget {
  final Function(int menuItemId) onItemTap;
  final VoidCallback onCartTap;
  final VoidCallback onSearchTap;

  const MenuScreen({
    super.key,
    required this.onItemTap,
    required this.onCartTap,
    required this.onSearchTap,
  });

  @override
  State<MenuScreen> createState() => _MenuScreenState();
}

class _MenuScreenState extends State<MenuScreen> with SingleTickerProviderStateMixin {
  TabController? _tabController;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final categories = context.watch<MenuProvider>().menuCategories;
    if (categories.isNotEmpty &&
        (_tabController == null || _tabController!.length != categories.length)) {
      _tabController?.dispose();
      _tabController = TabController(length: categories.length, vsync: this);
    }
  }

  @override
  void dispose() {
    _tabController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final menu = context.watch<MenuProvider>();
    final cart = context.watch<CartProvider>();

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            // Header
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
              child: Row(
                children: [
                  Expanded(
                    child: InkWell(
                      onTap: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => BranchSelectScreen(
                              onBranchSelected: (branch) {
                                context.read<MenuProvider>().selectBranch(branch);
                                Navigator.of(context).pop();
                              },
                            ),
                          ),
                        );
                      },
                      borderRadius: BorderRadius.circular(8),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(vertical: 4),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Text(
                                  menu.selectedBranch?.name ?? 'Select Branch',
                                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                        fontWeight: FontWeight.bold,
                                      ),
                                ),
                                const SizedBox(width: 4),
                                const Icon(Icons.keyboard_arrow_down, size: 20, color: AppTheme.primary),
                              ],
                            ),
                            if (menu.selectedBranch?.location != null)
                              Text(
                                menu.selectedBranch!.location!,
                                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                      color: AppTheme.textSecondary,
                                    ),
                              ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: widget.onSearchTap,
                    icon: const Icon(Icons.search, color: AppTheme.textPrimary),
                  ),
                  Stack(
                    children: [
                      IconButton(
                        onPressed: widget.onCartTap,
                        icon: const Icon(Icons.shopping_bag_outlined,
                            color: AppTheme.textPrimary),
                      ),
                      if (cart.itemCount > 0)
                        Positioned(
                          right: 4,
                          top: 4,
                          child: Container(
                            padding: const EdgeInsets.all(4),
                            decoration: const BoxDecoration(
                              color: AppTheme.primary,
                              shape: BoxShape.circle,
                            ),
                            child: Text(
                              '${cart.itemCount}',
                              style: const TextStyle(
                                fontSize: 10,
                                fontWeight: FontWeight.w700,
                                color: AppTheme.background,
                              ),
                            ),
                          ),
                        ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            
            // Loyalty Pulse (Glassmorphism card)
            _buildLoyaltyPulse(context),
            
            const SizedBox(height: 24),
            
            // Featured Items
            if (menu.menuCategories.isNotEmpty) ...[
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 24),
                child: Text(
                  'Featured for you',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w800,
                    letterSpacing: -0.5,
                  ),
                ),
              ),
              const SizedBox(height: 12),
              _buildFeaturedSection(menu),
              const SizedBox(height: 24),
            ],

            // Category tabs
            if (menu.menuCategories.isNotEmpty && _tabController != null)
              Container(
                height: 44,
                margin: const EdgeInsets.symmetric(horizontal: 16),
                child: TabBar(
                  controller: _tabController,
                  isScrollable: true,
                  tabAlignment: TabAlignment.start,
                  labelColor: AppTheme.background,
                  unselectedLabelColor: AppTheme.textSecondary,
                  indicator: BoxDecoration(
                    color: AppTheme.primary,
                    borderRadius: BorderRadius.circular(22),
                    boxShadow: [
                      BoxShadow(
                        color: AppTheme.primary.withValues(alpha: 0.3),
                        blurRadius: 8,
                        offset: const Offset(0, 2),
                      ),
                    ],
                  ),
                  dividerColor: Colors.transparent,
                  labelPadding: const EdgeInsets.symmetric(horizontal: 4),
                  tabs: menu.menuCategories
                      .map((c) => Tab(
                            child: Container(
                              padding: const EdgeInsets.symmetric(horizontal: 20),
                              child: Text(c.name,
                                  style: const TextStyle(
                                      fontSize: 14, fontWeight: FontWeight.w700)),
                            ),
                          ))
                      .toList(),
                ),
              ),
            const SizedBox(height: 8),
            // Menu grid
            Expanded(
              child: menu.isLoading
                  ? _buildShimmer()
                  : menu.menuCategories.isEmpty
                      ? const Center(child: Text('No menu items available'))
                      : TabBarView(
                          controller: _tabController,
                          children: menu.menuCategories
                              .map((cat) => _buildMenuGrid(cat.items))
                              .toList(),
                        ),
            ),
          ],
        ),
      ),
      // Floating cart bar
      bottomNavigationBar: cart.isEmpty
          ? null
          : Container(
              margin: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppTheme.primary,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: AppTheme.primary.withValues(alpha: 0.3),
                    blurRadius: 16,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Material(
                color: Colors.transparent,
                child: InkWell(
                  onTap: widget.onCartTap,
                  borderRadius: BorderRadius.circular(16),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                    child: Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: AppTheme.background.withValues(alpha: 0.2),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            '${cart.itemCount}',
                            style: const TextStyle(
                              color: AppTheme.background,
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        const Expanded(
                          child: Text(
                            'View Cart',
                            style: TextStyle(
                              color: AppTheme.background,
                              fontWeight: FontWeight.w600,
                              fontSize: 16,
                            ),
                          ),
                        ),
                        Text(
                          '\$${cart.subtotal.toStringAsFixed(2)}',
                          style: const TextStyle(
                            color: AppTheme.background,
                            fontWeight: FontWeight.w700,
                            fontSize: 16,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
    );
  }

  Widget _buildLoyaltyPulse(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    final customer = auth.customer;
    if (customer == null) return const SizedBox.shrink();

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [AppTheme.surface, AppTheme.surfaceLight.withValues(alpha: 0.5)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: AppTheme.primary.withValues(alpha: 0.1), width: 1.5),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: AppTheme.primary.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.stars_rounded, color: AppTheme.primary, size: 28),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${customer.loyaltyPoints} Points',
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w800,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  '${customer.membershipLevel} Status',
                  style: TextStyle(
                    fontSize: 12,
                    color: AppTheme.textSecondary,
                    fontWeight: FontWeight.w600,
                    letterSpacing: 0.5,
                  ),
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                'Gold Rewards',
                style: TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w800, fontSize: 13),
              ),
              const Icon(Icons.arrow_forward_ios_rounded, size: 12, color: AppTheme.primary),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildFeaturedSection(MenuProvider menu) {
    // Just pick first 3 items from first category for demo
    final items = menu.menuCategories.isNotEmpty ? menu.menuCategories.first.items.take(3).toList() : [];
    if (items.isEmpty) return const SizedBox.shrink();

    return SizedBox(
      height: 180,
      child: ListView.builder(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        scrollDirection: Axis.horizontal,
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return Container(
            width: 300,
            margin: const EdgeInsets.only(right: 16),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(24),
              image: item.imageUrl != null ? DecorationImage(
                image: CachedNetworkImageProvider(item.imageUrl!),
                fit: BoxFit.cover,
                colorFilter: ColorFilter.mode(Colors.black.withValues(alpha: 0.3), BlendMode.darken),
              ) : null,
              color: AppTheme.surface,
            ),
            child: InkWell(
              onTap: () => widget.onItemTap(item.menuItemId),
              borderRadius: BorderRadius.circular(24),
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Text(
                      item.name,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.5,
                      ),
                    ),
                    Text(
                      'Starting from \$${item.displayPrice.toStringAsFixed(2)}',
                      style: TextStyle(
                        color: Colors.white.withValues(alpha: 0.8),
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildMenuGrid(List<MenuItem> items) {
    return RefreshIndicator(
      onRefresh: () async {
        await context.read<MenuProvider>().loadBranches();
        if (mounted) {
          await context.read<AuthProvider>().refreshProfile();
        }
      },
      color: AppTheme.primary,
      backgroundColor: AppTheme.surface,
      child: GridView.builder(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 120), // Extra bottom padding for FloatingNavBar
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.7, // Slightly taller cards for premium look
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
        ),
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return _MenuItemCard(
            item: item,
            onTap: () => widget.onItemTap(item.menuItemId),
          );
        },
      ),
    );
  }

  Widget _buildShimmer() {
    return Shimmer.fromColors(
      baseColor: AppTheme.surface,
      highlightColor: AppTheme.surfaceLight,
      child: GridView.builder(
        padding: const EdgeInsets.all(16),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
        ),
        itemCount: 6,
        itemBuilder: (_, __) {
          return Container(
            decoration: BoxDecoration(
              color: AppTheme.surface,
              borderRadius: BorderRadius.circular(16),
            ),
          );
        },
      ),
    );
  }
}

class _MenuItemCard extends StatelessWidget {
  final MenuItem item;
  final VoidCallback onTap;

  const _MenuItemCard({required this.item, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Image
            Expanded(
              flex: 3,
              child: SizedBox(
                width: double.infinity,
                child: Hero(
                  tag: 'item_image_${item.menuItemId}',
                  child: item.imageUrl != null
                      ? CachedNetworkImage(
                          imageUrl: item.imageUrl!,
                          fit: BoxFit.cover,
                          placeholder: (_, __) => Container(
                            color: AppTheme.surfaceLight,
                            child: const Icon(Icons.coffee, color: AppTheme.textSecondary),
                          ),
                          errorWidget: (_, __, ___) => Container(
                            color: AppTheme.surfaceLight,
                            child: const Icon(Icons.coffee, color: AppTheme.textSecondary),
                          ),
                        )
                      : Container(
                          color: AppTheme.surfaceLight,
                          child: const Icon(Icons.coffee, size: 40, color: AppTheme.textSecondary),
                        ),
                ),
              ),
            ),
            // Info
            Expanded(
              flex: 2,
              child: Padding(
                padding: const EdgeInsets.all(10),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      item.name,
                      style: const TextStyle(
                        fontWeight: FontWeight.w600,
                        fontSize: 13,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          '\$${item.displayPrice.toStringAsFixed(2)}',
                          style: const TextStyle(
                            color: AppTheme.primary,
                            fontWeight: FontWeight.w700,
                            fontSize: 15,
                          ),
                        ),
                        if (!item.isAvailable)
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                            decoration: BoxDecoration(
                              color: AppTheme.error.withValues(alpha: 0.15),
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: const Text(
                              'Sold out',
                              style: TextStyle(color: AppTheme.error, fontSize: 10),
                            ),
                          ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
