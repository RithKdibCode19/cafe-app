import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shimmer/shimmer.dart';
import '../../config/theme.dart';
import '../../models/branch.dart';
import '../../providers/menu_provider.dart';
import '../../providers/cart_provider.dart';

class BranchSelectScreen extends StatefulWidget {
  final Function(Branch) onBranchSelected;

  const BranchSelectScreen({super.key, required this.onBranchSelected});

  @override
  State<BranchSelectScreen> createState() => _BranchSelectScreenState();
}

class _BranchSelectScreenState extends State<BranchSelectScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        context.read<MenuProvider>().loadBranches();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          color: AppTheme.background,
        ),
        child: SafeArea(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.fromLTRB(32, 40, 32, 24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: AppTheme.primary,
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: const Icon(Icons.coffee_rounded, color: AppTheme.background, size: 24),
                    ),
                    const SizedBox(height: 24),
                    Text(
                      'Select Branch',
                      style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                            fontWeight: FontWeight.w900,
                            letterSpacing: -1,
                          ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Choose a destination to experience our premium craft coffee.',
                      style: TextStyle(
                        color: AppTheme.textSecondary,
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                        height: 1.4,
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Consumer<MenuProvider>(
                  builder: (context, menu, _) {
                    if (menu.isLoading) {
                      return _buildShimmer();
                    }
                    if (menu.error != null) {
                      return Center(
                        child: Padding(
                          padding: const EdgeInsets.all(32),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(Icons.cloud_off_rounded, size: 64, color: AppTheme.surfaceLight),
                              const SizedBox(height: 24),
                              Text(
                                menu.error!,
                                textAlign: TextAlign.center,
                                style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w600),
                              ),
                              const SizedBox(height: 24),
                              SizedBox(
                                width: 140,
                                height: 48,
                                child: ElevatedButton(
                                  onPressed: () => menu.loadBranches(),
                                  child: const Text('RETRY'),
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    }
                    return ListView.builder(
                      padding: const EdgeInsets.fromLTRB(24, 8, 24, 40),
                      itemCount: menu.branches.length,
                      itemBuilder: (context, index) {
                        final branch = menu.branches[index];
                        return _BranchCard(
                          branch: branch,
                          onTap: () {
                            context.read<CartProvider>().setBranch(branch.branchId);
                            menu.selectBranch(branch);
                            widget.onBranchSelected(branch);
                          },
                        );
                      },
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildShimmer() {
    return Shimmer.fromColors(
      baseColor: AppTheme.surface,
      highlightColor: AppTheme.surfaceLight.withValues(alpha: 0.5),
      child: ListView.builder(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        itemCount: 3,
        itemBuilder: (_, __) {
          return Container(
            margin: const EdgeInsets.only(bottom: 20),
            height: 120,
            decoration: BoxDecoration(
              color: AppTheme.surface,
              borderRadius: BorderRadius.circular(24),
            ),
          );
        },
      ),
    );
  }
}

class _BranchCard extends StatelessWidget {
  final Branch branch;
  final VoidCallback onTap;

  const _BranchCard({required this.branch, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(28),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.2),
            blurRadius: 15,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(28),
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Row(
            children: [
              Container(
                width: 64,
                height: 64,
                decoration: BoxDecoration(
                  color: AppTheme.primary.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: const Icon(Icons.storefront_rounded, color: AppTheme.primary, size: 32),
              ),
              const SizedBox(width: 20),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      branch.name,
                      style: const TextStyle(
                        fontWeight: FontWeight.w900,
                        fontSize: 17,
                        letterSpacing: -0.2,
                      ),
                    ),
                    const SizedBox(height: 6),
                    if (branch.location != null)
                      Row(
                        children: [
                          const Icon(Icons.location_on_rounded, size: 14, color: AppTheme.primary),
                          const SizedBox(width: 6),
                          Expanded(
                            child: Text(
                              branch.location!,
                              style: TextStyle(
                                color: AppTheme.textSecondary,
                                fontSize: 13,
                                fontWeight: FontWeight.w600,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ],
                      ),
                  ],
                ),
              ),
              Container(
                width: 32,
                height: 32,
                decoration: BoxDecoration(
                  color: AppTheme.surfaceLight.withValues(alpha: 0.3),
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.chevron_right_rounded, color: AppTheme.primary, size: 20),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
