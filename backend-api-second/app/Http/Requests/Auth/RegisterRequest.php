<?php

namespace App\Http\Requests\Auth;

use App\Repositories\Auth\UserRepositoryInterface;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class RegisterRequest extends FormRequest
{
    public function __construct(
        private UserRepositoryInterface $userRepository
    ) {}

    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'name' => 'required|string|max:255|min:2',
            'email' => [
                'required',
                'email:rfc,dns',
                'max:255',
                Rule::unique('users', 'email')
            ],
            'password' => 'required|string|min:8|max:255|confirmed',
            'password_confirmation' => 'required|string|min:8|max:255',
        ];
    }

    public function messages(): array
    {
        return [
            'name.required' => 'Tên là bắt buộc.',
            'name.min' => 'Tên phải có ít nhất 2 ký tự.',
            'email.required' => 'Email là bắt buộc.',
            'email.email' => 'Email không đúng định dạng.',
            'email.unique' => 'Email đã được sử dụng.',
            'password.required' => 'Mật khẩu là bắt buộc.',
            'password.min' => 'Mật khẩu phải có ít nhất 8 ký tự.',
            'password.confirmed' => 'Mật khẩu xác nhận không khớp.',
            'password_confirmation.required' => 'Vui lòng xác nhận mật khẩu.',
        ];
    }

    protected function prepareForValidation(): void
    {
        $this->merge([
            'email' => strtolower(trim($this->email)),
            'name' => trim($this->name),
        ]);
    }

    public function validated($key = null, $default = null)
    {
        $validated = parent::validated($key, $default);

        // Remove password_confirmation from validated data
        unset($validated['password_confirmation']);

        return $validated;
    }
}
