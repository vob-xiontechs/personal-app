import { useState } from "react";
import { ProfileForm } from "./ProfileForm";
import { ProfilePresenter } from "./ProfilePresenter";
import { CreateProfileUseCase } from "../../application/profile/CreateProfileUseCase";
import { ProfileApiRepository } from "../../infrastructure/http/ProfileApiRepository";
import { HttpClient } from "../../infrastructure/http/HttpClient";
import { ProfileDomainService } from "../../domain/profile/services/ProfileDomainService";

export const ProfileContainer = () => {
  const useCase = new CreateProfileUseCase(
    new ProfileApiRepository(new HttpClient(), false),
    new ProfileDomainService()
  );

  const [values, setValues] = useState(ProfilePresenter.initialState());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(undefined);

    try {
      await useCase.execute(values);
      alert("Profile created successfully");
      setValues(ProfilePresenter.initialState());
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ProfileForm
      values={values}
      loading={loading}
      error={error}
      onChange={handleChange}
      onSubmit={handleSubmit}
    />
  );
};
