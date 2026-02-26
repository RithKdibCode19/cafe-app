import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../config/theme.dart';
import '../../providers/auth_provider.dart';
import '../../services/api_service.dart';

class RegisterScreen extends StatefulWidget {
  final VoidCallback? onLoginTap;

  const RegisterScreen({super.key, this.onLoginTap});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _phoneController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  String? _serverError;

  @override
  void dispose() {
    _nameController.dispose();
    _phoneController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  String _friendlyError(String raw) {
    final lower = raw.toLowerCase();
    if (lower.contains('phone number already registered')) {
      return 'This phone number is already registered. Please sign in instead.';
    }
    if (lower.contains('connection') || lower.contains('socketexception') || lower.contains('timeout')) {
      return 'Unable to connect to the server. Please check your internet connection.';
    }
    final cleaned = raw.replaceFirst(RegExp(r'^(ApiException|Exception):\s*', caseSensitive: false), '');
    return cleaned.isNotEmpty ? cleaned : 'Something went wrong. Please try again.';
  }

  Future<void> _register() async {
    setState(() => _serverError = null);
    if (!_formKey.currentState!.validate()) return;

    final auth = context.read<AuthProvider>();
    final success = await auth.register(
      name: _nameController.text.trim(),
      phone: _phoneController.text.trim(),
      password: _passwordController.text,
    );
    if (!success && mounted) {
      setState(() {
        _serverError = _friendlyError(auth.error ?? 'Registration failed');
      });
    }
  }

  // Warm palette (matches login)
  static const Color _bgCream = Color(0xFFF5EDE3);
  static const Color _cardBg = Color(0xFFF7F0E8);
  static const Color _brownDark = Color(0xFF5C3D2E);
  static const Color _brownMedium = Color(0xFF8B6544);
  static const Color _brownLight = Color(0xFFC69C6D);
  static const Color _brownBtn = Color(0xFF9B6B47);
  static const Color _inputBg = Color(0xFFFFFCF8);
  static const Color _inputBorder = Color(0xFFE8DDD0);
  static const Color _hintColor = Color(0xFFBBA898);
  static const Color _subtitleColor = Color(0xFF9E8B7D);
  static const Color _errorColor = Color(0xFFD32F2F);
  static const Color _dividerBrown = Color(0xFFD4B79A);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // Background Image
          Positioned.fill(
            child: Image.asset(
              'assets/images/cafe_bg.png',
              fit: BoxFit.cover,
              errorBuilder: (_, __, ___) => Container(color: _bgCream),
            ),
          ),

          // Main card
          SafeArea(
            child: Center(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 40),
                  decoration: BoxDecoration(
                    color: _cardBg,
                    borderRadius: BorderRadius.circular(32),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withValues(alpha: 0.08),
                        blurRadius: 32,
                        spreadRadius: 2,
                        offset: const Offset(0, 8),
                      ),
                    ],
                  ),
                  child: Form(
                    key: _formKey,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Logo badge
                        Container(
                          width: 100,
                          height: 100,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: Colors.white,
                            boxShadow: [
                              BoxShadow(
                                color: Colors.black.withValues(alpha: 0.1),
                                blurRadius: 16,
                                offset: const Offset(0, 6),
                              ),
                            ],
                          ),
                          child: Padding(
                            padding: const EdgeInsets.all(16),
                            child: Image.asset(
                              'assets/images/logo_app.png',
                              fit: BoxFit.contain,
                            ),
                          ),
                        ),
                        const SizedBox(height: 28),

                        // Welcome text
                        Text(
                          'Welcome Cafe Shop! Please\nsign up to continue.',
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            fontSize: 15,
                            fontWeight: FontWeight.w500,
                            color: _subtitleColor,
                            height: 1.5,
                          ),
                        ),

                        const SizedBox(height: 40),

                        // Server error banner
                        if (_serverError != null)
                          Container(
                            width: double.infinity,
                            margin: const EdgeInsets.only(bottom: 20),
                            padding: const EdgeInsets.all(14),
                            decoration: BoxDecoration(
                              color: _errorColor.withValues(alpha: 0.08),
                              borderRadius: BorderRadius.circular(14),
                              border: Border.all(color: _errorColor.withValues(alpha: 0.3)),
                            ),
                            child: Row(
                              children: [
                                Icon(Icons.error_outline_rounded, color: _errorColor, size: 20),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Text(
                                    _serverError!,
                                    style: TextStyle(color: _errorColor, fontWeight: FontWeight.w600, fontSize: 13, height: 1.3),
                                  ),
                                ),
                              ],
                            ),
                          ),

                        // Name field
                        _buildField(
                          controller: _nameController,
                          hintText: 'Full name',
                          icon: Icons.person_outline,
                          textCapitalization: TextCapitalization.words,
                          validator: (value) {
                            if (value == null || value.trim().isEmpty) {
                              return 'Please enter your name';
                            }
                            if (value.trim().length < 2) {
                              return 'Name must be at least 2 characters';
                            }
                            return null;
                          },
                        ),
                        const SizedBox(height: 16),

                        // Phone field
                        _buildField(
                          controller: _phoneController,
                          hintText: 'Phone number',
                          icon: Icons.phone_outlined,
                          keyboardType: TextInputType.phone,
                          validator: (value) {
                            if (value == null || value.trim().isEmpty) {
                              return 'Please enter your phone number';
                            }
                            final cleaned = value.trim().replaceAll(RegExp(r'[\s\-\(\)]'), '');
                            if (cleaned.length < 8 || cleaned.length > 15) {
                              return 'Phone number must be 8-15 digits';
                            }
                            if (!RegExp(r'^[0-9+]+$').hasMatch(cleaned)) {
                              return 'Phone number can only contain digits';
                            }
                            return null;
                          },
                        ),
                        const SizedBox(height: 16),

                        // Password field
                        _buildField(
                          controller: _passwordController,
                          hintText: '••••••••••••',
                          icon: Icons.lock_outline_rounded,
                          obscureText: _obscurePassword,
                          suffixIcon: IconButton(
                            icon: Icon(
                              _obscurePassword ? Icons.visibility_off_outlined : Icons.visibility_outlined,
                              size: 20,
                              color: _hintColor,
                            ),
                            onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                          ),
                          validator: (value) {
                            if (value == null || value.isEmpty) {
                              return 'Please create a password';
                            }
                            if (value.length < 6) {
                              return 'Password must be at least 6 characters';
                            }
                            return null;
                          },
                          onSubmitted: (_) => _register(),
                        ),

                        const SizedBox(height: 32),

                        // Sign Up Button
                        Consumer<AuthProvider>(
                          builder: (context, auth, _) {
                            return SizedBox(
                              width: double.infinity,
                              height: 54,
                              child: ElevatedButton(
                                onPressed: auth.isLoading ? null : _register,
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: _brownBtn,
                                  foregroundColor: Colors.white,
                                  disabledBackgroundColor: _brownBtn.withValues(alpha: 0.5),
                                  elevation: 0,
                                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                                ),
                                child: auth.isLoading
                                    ? const SizedBox(
                                        height: 20, width: 20,
                                        child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                                      )
                                    : const Text('Sign up',
                                        style: TextStyle(fontWeight: FontWeight.w700, fontSize: 17, letterSpacing: 0.3)),
                              ),
                            );
                          },
                        ),

                        const SizedBox(height: 24),

                        // OR divider
                        Row(
                          children: [
                            Expanded(child: Divider(color: _dividerBrown, thickness: 1)),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 20),
                              child: Text('OR',
                                  style: TextStyle(color: _subtitleColor, fontWeight: FontWeight.w600, fontSize: 13)),
                            ),
                            Expanded(child: Divider(color: _dividerBrown, thickness: 1)),
                          ],
                        ),

                        const SizedBox(height: 24),

                        // Login link
                        GestureDetector(
                          onTap: widget.onLoginTap,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                "Already have an account?   ",
                                style: TextStyle(color: _subtitleColor, fontWeight: FontWeight.w500, fontSize: 14),
                              ),
                              Text(
                                'Sign In',
                                style: TextStyle(color: _brownDark, fontWeight: FontWeight.w800, fontSize: 14),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildField({
    required TextEditingController controller,
    required String hintText,
    required IconData icon,
    TextInputType? keyboardType,
    TextCapitalization textCapitalization = TextCapitalization.none,
    bool obscureText = false,
    Widget? suffixIcon,
    String? Function(String?)? validator,
    ValueChanged<String>? onSubmitted,
  }) {
    return TextFormField(
      controller: controller,
      keyboardType: keyboardType,
      textCapitalization: textCapitalization,
      obscureText: obscureText,
      onFieldSubmitted: onSubmitted,
      validator: validator,
      autovalidateMode: AutovalidateMode.onUserInteraction,
      style: TextStyle(color: _brownDark, fontWeight: FontWeight.w600, fontSize: 14),
      decoration: InputDecoration(
        hintText: hintText,
        hintStyle: TextStyle(color: _hintColor, fontWeight: FontWeight.w500),
        prefixIcon: Icon(icon, color: _hintColor, size: 22),
        suffixIcon: suffixIcon,
        filled: true,
        fillColor: _inputBg,
        errorStyle: TextStyle(color: _errorColor, fontWeight: FontWeight.w600, fontSize: 11),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(14), borderSide: BorderSide(color: _inputBorder, width: 0.5)),
        enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(14), borderSide: BorderSide(color: _inputBorder, width: 0.5)),
        focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(14), borderSide: BorderSide(color: _brownLight, width: 1.5)),
        errorBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(14), borderSide: BorderSide(color: _errorColor.withValues(alpha: 0.5))),
        focusedErrorBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(14), borderSide: BorderSide(color: _errorColor, width: 1.5)),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
      ),
    );
  }
}
