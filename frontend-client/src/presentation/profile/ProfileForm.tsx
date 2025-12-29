import { TextInput, Button } from "@carbon/react";
import "./profile-form.scss";

interface Props {
  values: {
    name: string;
    email: string;
    password: string;
  };
  loading: boolean;
  error?: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: () => void;
}

export const ProfileForm = ({
  values,
  loading,
  error,
  onChange,
  onSubmit,
}: Props) => (
  <div className="profile-form">
    <h2 className="profile-form__title">Create profile</h2>

    <div className="profile-form__field">
      <TextInput
        id="name"
        name="name"
        labelText="Name"
        value={values.name}
        onChange={onChange}
      />
    </div>

    <div className="profile-form__field">
      <TextInput
        id="email"
        name="email"
        labelText="Email"
        value={values.email}
        onChange={onChange}
      />
    </div>

    <div className="profile-form__field">
      <TextInput
        id="password"
        name="password"
        type="password"
        labelText="Password"
        value={values.password}
        onChange={onChange}
      />
    </div>

    {error && <p className="profile-form__error">{error}</p>}

    <div className="profile-form__actions">
      <Button kind="primary" disabled={loading} onClick={onSubmit}>
        {loading ? "Submitting..." : "Submit"}
      </Button>
    </div>
  </div>
);
