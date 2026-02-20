import 'package:flutter/material.dart';
import '../models/branch.dart';
import '../models/menu_item.dart';
import '../services/api_service.dart';
import '../services/menu_service.dart';

class MenuProvider extends ChangeNotifier {
  late final MenuService _menuService;

  List<Branch> _branches = [];
  Branch? _selectedBranch;
  List<CategoryWithItems> _menuCategories = [];
  bool _isLoading = false;
  String? _error;

  MenuProvider(ApiService apiService) {
    _menuService = MenuService(apiService);
  }

  List<Branch> get branches => _branches;
  Branch? get selectedBranch => _selectedBranch;
  List<CategoryWithItems> get menuCategories => _menuCategories;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadBranches() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _branches = await _menuService.getBranches();
      
      // Auto-select first branch if none selected
      if (_selectedBranch == null && _branches.isNotEmpty) {
        _selectedBranch = _branches.first;
        await loadBranchMenu(_selectedBranch!.branchId);
      }
      
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> selectBranch(Branch branch) async {
    _selectedBranch = branch;
    notifyListeners();
    await loadBranchMenu(branch.branchId);
  }

  Future<void> loadBranchMenu(int branchId) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final data = await _menuService.getBranchMenu(branchId);
      _menuCategories = (data['categories'] as List<dynamic>?)
              ?.map((c) => CategoryWithItems.fromJson(c))
              .toList() ??
          [];
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<MenuItemDetail> getItemDetail(int menuItemId) async {
    return await _menuService.getItemDetail(menuItemId);
  }

  Future<List<MenuItem>> searchMenu(String query) async {
    return await _menuService.searchMenu(query);
  }
}
