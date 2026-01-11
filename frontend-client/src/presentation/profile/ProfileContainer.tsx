import { useState, useEffect } from "react";
import { ProfileForm } from "./ProfileForm";
import { ProfileList } from "./ProfileList";
import { ProfilePresenter } from "./ProfilePresenter";
import { CreateProfileUseCase } from "../../application/profile/CreateProfileUseCase";
import { GetProfileListUseCase } from "../../application/profile/GetProfileListUseCase";
import { ProfileApiRepository } from "../../infrastructure/http/ProfileApiRepository";
import { ProfileDomainService } from "../../domain/profile/services/ProfileDomainService";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

export const ProfileContainer = () => {
  // Use only REST API with Axios
  const [repository] = useState(() => new ProfileApiRepository());

  const createUseCase = new CreateProfileUseCase(
    repository,
    new ProfileDomainService()
  );

  const getListUseCase = new GetProfileListUseCase(repository);

  // Form state
  const [values, setValues] = useState(ProfilePresenter.initialState());
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string>();

  // List state
  const [profiles, setProfiles] = useState<ProfileResponse[]>([]);
  const [listLoading, setListLoading] = useState(false);
  const [listError, setListError] = useState<string | null>(null);

  // Load profiles on mount
  useEffect(() => {
    loadProfiles();
  }, []);

  const loadProfiles = async () => {
    setListLoading(true);
    setListError(null);

    try {
      const profileList = await getListUseCase.execute();
      setProfiles(profileList);
    } catch (e: any) {
      setListError(e.message);
    } finally {
      setListLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    setFormLoading(true);
    setFormError(undefined);

    try {
      await createUseCase.execute(values);
      alert("Profile created successfully");
      setValues(ProfilePresenter.initialState());
      // Refresh the list after creating
      loadProfiles();
    } catch (e: any) {
      setFormError(e.message);
    } finally {
      setFormLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{
        textAlign: 'center',
        fontSize: '2.5rem',
        fontWeight: '700',
        marginBottom: '2rem',
        background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        backgroundClip: 'text'
      }}>
        Profile Management
      </h1>

      <section style={{ marginBottom: '3rem' }}>
        <h2 style={{
          fontSize: '1.75rem',
          fontWeight: '700',
          marginBottom: '1.5rem',
          textAlign: 'center',
          background: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text'
        }}>
          Create New Profile
        </h2>
        <ProfileForm
          values={values}
          loading={formLoading}
          error={formError}
          onChange={handleChange}
          onSubmit={handleSubmit}
        />
      </section>

      <section>
        <ProfileList
          profiles={profiles}
          loading={listLoading}
          error={listError}
          onRefresh={loadProfiles}
        />
      </section>
    </div>
  );
};
