import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../config/theme.dart';
import '../../models/menu_item.dart';
import '../../providers/menu_provider.dart';

class SearchScreen extends StatefulWidget {
  final Function(int menuItemId) onItemTap;

  const SearchScreen({super.key, required this.onItemTap});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final _searchController = TextEditingController();
  List<MenuItem> _results = [];
  bool _isSearching = false;

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _search(String query) async {
    if (query.trim().length < 2) {
      if (mounted) {
        setState(() => _results = []);
      }
      return;
    }
    setState(() => _isSearching = true);
    try {
      final results = await context.read<MenuProvider>().searchMenu(query.trim());
      if (mounted) {
        setState(() {
          _results = results;
          _isSearching = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isSearching = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leadingWidth: 40,
        titleSpacing: 0,
        title: Container(
          height: 48,
          margin: const EdgeInsets.only(right: 16),
          decoration: BoxDecoration(
            color: AppTheme.surface,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppTheme.surfaceLight.withValues(alpha: 0.5)),
          ),
          child: TextField(
            controller: _searchController,
            autofocus: true,
            style: const TextStyle(color: AppTheme.textPrimary, fontWeight: FontWeight.w600, fontSize: 15),
            decoration: InputDecoration(
              hintText: 'Search our craft menu...',
              border: InputBorder.none,
              prefixIcon: const Icon(Icons.search_rounded, color: AppTheme.primary, size: 20),
              hintStyle: TextStyle(color: AppTheme.textSecondary, fontSize: 13, fontWeight: FontWeight.normal),
              suffixIcon: _searchController.text.isNotEmpty
                  ? IconButton(
                      icon: const Icon(Icons.cancel_rounded, color: AppTheme.textSecondary, size: 18),
                      onPressed: () {
                        _searchController.clear();
                        setState(() => _results = []);
                      },
                    )
                  : null,
            ),
            onChanged: _search,
          ),
        ),
        elevation: 0,
      ),
      body: _isSearching
          ? const Center(child: CircularProgressIndicator(color: AppTheme.primary))
          : _results.isEmpty
              ? Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Container(
                        padding: const EdgeInsets.all(24),
                        decoration: BoxDecoration(color: AppTheme.surface, shape: BoxShape.circle),
                        child: Icon(
                          _searchController.text.isEmpty ? Icons.travel_explore_rounded : Icons.search_off_rounded,
                          size: 48,
                          color: AppTheme.primary.withValues(alpha: 0.5),
                        ),
                      ),
                      const SizedBox(height: 24),
                      Text(
                        _searchController.text.isEmpty ? 'Discover something new' : 'No results found',
                        style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 16),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        _searchController.text.isEmpty ? 'Type to find your favorite craft brew.' : 'Try a different keyword or category.',
                        style: TextStyle(color: AppTheme.textSecondary, fontSize: 13),
                      ),
                    ],
                  ),
                )
              : ListView.builder(
                  padding: const EdgeInsets.fromLTRB(20, 16, 20, 100),
                  itemCount: _results.length,
                  itemBuilder: (context, index) {
                    final item = _results[index];
                    return Container(
                      margin: const EdgeInsets.only(bottom: 12),
                      decoration: BoxDecoration(
                        color: AppTheme.surface,
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(color: AppTheme.surfaceLight.withValues(alpha: 0.5)),
                      ),
                      child: ListTile(
                        onTap: () => widget.onItemTap(item.menuItemId),
                        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        leading: Container(
                          width: 56,
                          height: 56,
                          decoration: BoxDecoration(
                            color: AppTheme.background,
                            borderRadius: BorderRadius.circular(14),
                          ),
                          child: const Icon(Icons.coffee_rounded, color: AppTheme.primary, size: 28),
                        ),
                        title: Text(item.name, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 15)),
                        subtitle: Padding(
                          padding: const EdgeInsets.only(top: 4),
                          child: Text(
                            item.categoryName ?? 'Craft',
                            style: TextStyle(color: AppTheme.textSecondary, fontSize: 11, fontWeight: FontWeight.w700, letterSpacing: 0.5),
                          ),
                        ),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              '\$${item.displayPrice.toStringAsFixed(2)}',
                              style: const TextStyle(
                                color: AppTheme.primary,
                                fontWeight: FontWeight.w900,
                                fontSize: 16,
                              ),
                            ),
                            const Icon(Icons.arrow_forward_ios_rounded, size: 10, color: AppTheme.surfaceLight),
                          ],
                        ),
                      ),
                    );
                  },
                ),
    );
  }
}
