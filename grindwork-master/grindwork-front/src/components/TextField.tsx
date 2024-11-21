import { FaUser, FaLock, FaAddressCard, FaEnvelope } from "react-icons/fa";

interface Props {
  placeholder?: string;
  icon?: string;
  value: string;
  onChange: any;
  name?:string;
}

const TextField = ({ placeholder, icon, value, onChange,name }: Props) => {
  let type, icone, max;
  let cn = " h-full mr-1 text-gray-800";

  switch (icon) {
    case "email":
      icone = <FaEnvelope className={cn} />;
      type = "text";
      max = 50;
      break;
    case "pwd":
      icone = <FaLock className={cn} />;
      type = "password";
      max = 50;
      break;
    case "dataUser":
      icone = <FaAddressCard className={cn} />;
      type = "text";
      max = 100;
      break;
    case "user":
      icone = <FaUser className={cn} />;
      type = "text";
      max = 100;
      break;
  }

  return (
    <>
      <div className="flex w-full mt-2 bg-slate-200 rounded-md">
        <div className="flex w-full ml-2">
          {icone}
          <input
            type={type}
            name={name}
            maxLength={max}
            placeholder={placeholder}
            value={value}
            onChange={onChange}
            required
            className="block w-full border h-12 p-2 bg-slate-200 rounded-md"
          />
        </div>
      </div>
    </>
  );
};

export default TextField;
