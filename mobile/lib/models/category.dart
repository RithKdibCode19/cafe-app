class Category {
  final int categoryId;
  final String name;
  final String? description;
  final int? parentId;
  final List<Category> children;

  Category({
    required this.categoryId,
    required this.name,
    this.description,
    this.parentId,
    this.children = const [],
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      categoryId: json['categoryId'],
      name: json['name'],
      description: json['description'],
      parentId: json['parentId'],
      children: (json['children'] as List<dynamic>?)
              ?.map((c) => Category.fromJson(c))
              .toList() ??
          [],
    );
  }
}
