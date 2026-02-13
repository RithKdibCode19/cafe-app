import '../models/branch.dart';
import '../models/menu_item.dart';
import 'api_service.dart';

class MenuService {
  final ApiService _api;

  MenuService(this._api);

  Future<List<Branch>> getBranches() async {
    final data = await _api.get('/branches');
    return (data as List).map((b) => Branch.fromJson(b)).toList();
  }

  Future<Map<String, dynamic>> getBranchMenu(int branchId) async {
    final data = await _api.get('/branches/$branchId/menu');
    return data;
  }

  Future<List<MenuItem>> searchMenu(String query) async {
    final data = await _api.get('/menu/search?q=$query');
    return (data as List).map((i) => MenuItem.fromJson(i)).toList();
  }

  Future<MenuItemDetail> getItemDetail(int menuItemId) async {
    final data = await _api.get('/menu/$menuItemId');
    return MenuItemDetail.fromJson(data);
  }

  Future<List<AddOn>> getAddOns() async {
    final data = await _api.get('/addons');
    return (data as List).map((a) => AddOn.fromJson(a)).toList();
  }
}
